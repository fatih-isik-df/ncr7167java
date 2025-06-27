package com.ncr.printer;

import com.fazecast.jSerialComm.SerialPort;
import com.ncr.printer.commands.Ncr7167Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Main class for communicating with NCR 7167 Two-Station POS Printer
 * Based on the NCR 7167 Owner's Manual specifications
 */
public class Ncr7167Printer {
    
    private static final Logger logger = LoggerFactory.getLogger(Ncr7167Printer.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    private final Ncr7167Config config;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isConnected = false;
    
    public Ncr7167Printer(Ncr7167Config config) {
        this.config = config;
    }
    
    /**
     * Connect to the printer
     */
    public void connect() throws Ncr7167Exception {
        if (isConnected) {
            logger.warn("Printer is already connected");
            return;
        }
        
        if (config.getPortName() == null || config.getPortName().isEmpty()) {
            throw new Ncr7167Exception("Port name is not configured");
        }
        
        try {
            serialPort = SerialPort.getCommPort(config.getPortName());
            
            // Configure serial port based on config
            serialPort.setBaudRate(config.getBaudRate());
            serialPort.setNumDataBits(config.getDataBits());
            serialPort.setNumStopBits(config.getStopBits());
            serialPort.setParity(config.getParity());
            
            // Set flow control
            switch (config.getFlowControl()) {
                case XON_XOFF:
                    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | 
                                             SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
                    break;
                case DTR_DSR:
                    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DTR_ENABLED | 
                                             SerialPort.FLOW_CONTROL_DSR_ENABLED);
                    break;
                default:
                    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            }
            
            // Set timeouts
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | 
                                         SerialPort.TIMEOUT_WRITE_BLOCKING, 
                                         config.getTimeout(), config.getTimeout());
            
            // Open the port
            if (!serialPort.openPort()) {
                throw new Ncr7167Exception("Failed to open serial port: " + config.getPortName());
            }
            
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
            isConnected = true;
            
            logger.info("Successfully connected to NCR 7167 printer on port: {}", config.getPortName());
            
            // Wait a moment for the connection to stabilize
            Thread.sleep(100);
            
        } catch (Exception e) {
            throw new Ncr7167Exception("Failed to connect to printer", e);
        }
    }
    
    /**
     * Disconnect from the printer
     */
    public void disconnect() {
        if (!isConnected) {
            return;
        }
        
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
            
            isConnected = false;
            logger.info("Disconnected from NCR 7167 printer");
            
        } catch (Exception e) {
            logger.error("Error while disconnecting from printer", e);
        }
    }
    
    /**
     * Check if printer is connected
     */
    public boolean isConnected() {
        return isConnected && serialPort != null && serialPort.isOpen();
    }
    
    /**
     * Send raw command bytes to the printer
     */
    public void sendCommand(byte[] command) throws Ncr7167Exception {
        if (!isConnected()) {
            throw new Ncr7167Exception("Printer is not connected");
        }
        
        try {
            logger.debug("Sending command: {}", Arrays.toString(command));
            outputStream.write(command);
            outputStream.flush();
            
        } catch (IOException e) {
            throw new Ncr7167Exception("Failed to send command to printer", e);
        }
    }
    
    /**
     * Send text to the printer
     */
    public void sendText(String text) throws Ncr7167Exception {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        byte[] textBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        sendCommand(textBytes);
    }
    
    /**
     * Print text and feed paper one line
     */
    public void printLine(String text) throws Ncr7167Exception {
        sendText(text);
        sendCommand(new byte[]{Ncr7167Commands.PRINT_AND_FEED_ONE_LINE});
    }
    
    /**
     * Print an empty line (line feed)
     */
    public void emptyLine() throws Ncr7167Exception {
        sendCommand(new byte[]{Ncr7167Commands.PRINT_AND_FEED_ONE_LINE});
    }
    
    /**
     * Initialize the printer
     */
    public void initialize() throws Ncr7167Exception {
        logger.info("Initializing NCR 7167 printer");
        sendCommand(Ncr7167Commands.EscCommands.INITIALIZE_PRINTER);
        
        // Wait for initialization to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Clear the printer buffer
     */
    public void clear() throws Ncr7167Exception {
        sendCommand(new byte[]{Ncr7167Commands.CLEAR_PRINTER});
    }
    
    /**
     * Select receipt station
     */
    public void selectReceiptStation() throws Ncr7167Exception {
        logger.debug("Selecting receipt station");
        sendCommand(new byte[]{Ncr7167Commands.SELECT_RECEIPT_STATION});
    }
    
    /**
     * Select slip station
     */
    public void selectSlipStation() throws Ncr7167Exception {
        logger.debug("Selecting slip station");
        sendCommand(new byte[]{Ncr7167Commands.SELECT_SLIP_STATION});
    }
    
    /**
     * Open cash drawer
     */
    public void openCashDrawer() throws Ncr7167Exception {
        // Default timing: 110ms on time (recommended for NCR cash drawers)
        openCashDrawer(55, 55); // 55 * 2ms = 110ms
    }
    
    /**
     * Open cash drawer with custom timing
     * @param onTime On time in 2ms units
     * @param offTime Off time in 2ms units
     */
    public void openCashDrawer(int onTime, int offTime) throws Ncr7167Exception {
        logger.debug("Opening cash drawer");
        byte[] command = {
            Ncr7167Commands.ESC, 0x70, 0x00, 
            (byte) onTime, (byte) offTime
        };
        sendCommand(command);
    }
    
    /**
     * Cut paper (full cut)
     */
    public void cutPaper() throws Ncr7167Exception {
        cutPaper(true);
    }
    
    /**
     * Cut paper
     * @param fullCut true for full cut, false for partial cut
     */
    public void cutPaper(boolean fullCut) throws Ncr7167Exception {
        logger.debug("Cutting paper (full cut: {})", fullCut);
        byte[] command = {
            Ncr7167Commands.GS, 0x56, (byte) (fullCut ? 0 : 1)
        };
        sendCommand(command);
    }
    
    /**
     * Enable/disable double-wide characters
     */
    public void setDoubleWide(boolean enable) throws Ncr7167Exception {
        if (enable) {
            sendCommand(new byte[]{Ncr7167Commands.SELECT_DOUBLE_WIDE});
        } else {
            sendCommand(new byte[]{Ncr7167Commands.SELECT_SINGLE_WIDE});
        }
    }
    
    /**
     * Set character pitch
     * @param pitch number of characters per line
     */
    public void setCharacterPitch(int pitch) throws Ncr7167Exception {
        byte[] command = {
            Ncr7167Commands.ESC, 0x16, (byte) pitch
        };
        sendCommand(command);
    }
    
    /**
     * Enable/disable emphasized (bold) text
     */
    public void setEmphasized(boolean enable) throws Ncr7167Exception {
        byte[] command = {
            Ncr7167Commands.ESC, 0x45, (byte) (enable ? 1 : 0)
        };
        sendCommand(command);
    }
    
    /**
     * Enable/disable underline
     */
    public void setUnderline(boolean enable) throws Ncr7167Exception {
        byte[] command = {
            Ncr7167Commands.ESC, 0x2D, (byte) (enable ? 1 : 0)
        };
        sendCommand(command);
    }
    
    /**
     * Set line spacing
     * @param spacing spacing in 1/406 inches for receipt, 1/144 inches for slip
     */
    public void setLineSpacing(int spacing) throws Ncr7167Exception {
        byte[] command = {
            Ncr7167Commands.ESC, 0x33, (byte) spacing
        };
        sendCommand(command);
    }
    
    /**
     * Set line spacing to 1/6 inch (default)
     */
    public void setDefaultLineSpacing() throws Ncr7167Exception {
        sendCommand(Ncr7167Commands.EscCommands.SET_LINE_SPACING_1_6_INCH);
    }
    
    /**
     * Print bar code
     * @param type bar code type (use BarCodeTypes constants)
     * @param data bar code data
     */
    public void printBarCode(int type, String data) throws Ncr7167Exception {
        if (data == null || data.isEmpty()) {
            throw new Ncr7167Exception("Bar code data cannot be empty");
        }
        
        logger.debug("Printing bar code type: {}, data: {}", type, data);
        
        // Build command: GS k type data NUL
        byte[] dataBytes = data.getBytes(StandardCharsets.US_ASCII);
        byte[] command = new byte[4 + dataBytes.length];
        
        command[0] = Ncr7167Commands.GS;
        command[1] = 0x6B;
        command[2] = (byte) type;
        
        System.arraycopy(dataBytes, 0, command, 3, dataBytes.length);
        command[command.length - 1] = Ncr7167Commands.NUL;
        
        sendCommand(command);
    }
    
    /**
     * Set bar code height
     * @param height height in dots (1-255)
     */
    public void setBarCodeHeight(int height) throws Ncr7167Exception {
        if (height < 1 || height > 255) {
            throw new Ncr7167Exception("Bar code height must be between 1 and 255");
        }
        
        byte[] command = {
            Ncr7167Commands.GS, 0x68, (byte) height
        };
        sendCommand(command);
    }
    
    /**
     * Request printer status
     * @return status byte from printer
     */
    public int requestStatus() throws Ncr7167Exception {
        try {
            // Send status request command
            sendCommand(Ncr7167Commands.GsCommands.REQUEST_PRINTER_STATUS);
            
            // Wait for response
            Thread.sleep(100);
            
            if (inputStream.available() > 0) {
                return inputStream.read();
            }
            
            throw new Ncr7167Exception("No status response from printer");
            
        } catch (IOException | InterruptedException e) {
            throw new Ncr7167Exception("Failed to request printer status", e);
        }
    }
    
    /**
     * Check if paper is present
     */
    public boolean isPaperPresent() throws Ncr7167Exception {
        int status = requestStatus();
        return (status & Ncr7167Commands.StatusBytes.RECEIPT_PAPER_OUT) == 0;
    }
    
    /**
     * Check if paper is low
     */
    public boolean isPaperLow() throws Ncr7167Exception {
        int status = requestStatus();
        return (status & Ncr7167Commands.StatusBytes.RECEIPT_PAPER_LOW) != 0;
    }
    
    /**
     * Feed paper n lines
     */
    public void feedPaper(int lines) throws Ncr7167Exception {
        if (lines < 1 || lines > 255) {
            throw new Ncr7167Exception("Lines to feed must be between 1 and 255");
        }
        
        byte[] command = {
            Ncr7167Commands.ESC, 0x64, (byte) lines
        };
        sendCommand(command);
    }
    
    /**
     * Get the current configuration
     */
    public Ncr7167Config getConfig() {
        return config;
    }
    
    /**
     * Perform a basic printer test
     */
    public void performSelfTest() throws Ncr7167Exception {
        logger.info("Performing printer self test");
        
        try {
            initialize();
            Thread.sleep(500);
            
            selectReceiptStation();
            printLine("=== NCR 7167 PRINTER TEST ===");
            printLine("");
            printLine("Date: " + java.time.LocalDateTime.now().toString());
            printLine("Port: " + config.getPortName());
            printLine("Baud Rate: " + config.getBaudRate());
            printLine("");
            
            // Test different formatting
            setEmphasized(true);
            printLine("EMPHASIZED TEXT");
            setEmphasized(false);
            
            setUnderline(true);
            printLine("UNDERLINED TEXT");
            setUnderline(false);
            
            setDoubleWide(true);
            printLine("DOUBLE WIDE");
            setDoubleWide(false);
            
            printLine("");
            printLine("Test completed successfully!");
            
            // Feed extra paper
            feedPaper(3);
            
            // Cut paper if available
            try {
                cutPaper();
            } catch (Exception e) {
                logger.debug("Paper cutting not available or failed", e);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Ncr7167Exception("Self test interrupted", e);
        }
    }
    
    /**
     * Auto-detect available serial ports
     */
    public static String[] getAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] portNames = new String[ports.length];
        
        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getSystemPortName();
        }
        
        return portNames;
    }
}
