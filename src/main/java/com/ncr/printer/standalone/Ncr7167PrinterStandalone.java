package com.ncr.printer.standalone;

import java.io.*;
import java.util.Arrays;

/**
 * Standalone NCR 7167 Printer class that doesn't require external dependencies
 * This version uses basic Java I/O for serial communication
 */
public class Ncr7167PrinterStandalone {
    
    private static final byte ESC = 0x1B;
    private static final byte GS = 0x1D;
    
    // Basic commands
    private static final byte INITIALIZE_PRINTER = 0x40; // ESC @
    private static final byte PRINT_AND_FEED = 0x0A;    // LF
    private static final byte SELECT_RECEIPT = 0x1E;    // RS
    private static final byte SELECT_SLIP = 0x1C;       // FS
    private static final byte CLEAR_PRINTER = 0x10;     // DLE
    
    private String portName;
    private FileOutputStream outputStream;
    private boolean isConnected = false;
    
    public Ncr7167PrinterStandalone(String portName) {
        this.portName = portName;
    }
    
    /**
     * Connect to printer (Windows COM port)
     */
    public void connect() throws IOException {
        try {
            // On Windows, we can try to open COM port as a file
            outputStream = new FileOutputStream(portName);
            isConnected = true;
            System.out.println("Connected to printer on " + portName);
        } catch (IOException e) {
            throw new IOException("Failed to connect to " + portName + ": " + e.getMessage());
        }
    }
    
    /**
     * Disconnect from printer
     */
    public void disconnect() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        isConnected = false;
        System.out.println("Disconnected from printer");
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Send raw command to printer
     */
    public void sendCommand(byte[] command) throws IOException {
        if (!isConnected || outputStream == null) {
            throw new IOException("Printer not connected");
        }
        
        System.out.println("Sending command: " + Arrays.toString(command));
        outputStream.write(command);
        outputStream.flush();
    }
    
    /**
     * Send text to printer
     */
    public void sendText(String text) throws IOException {
        if (text != null && !text.isEmpty()) {
            sendCommand(text.getBytes("ISO-8859-1"));
        }
    }
    
    /**
     * Initialize printer
     */
    public void initialize() throws IOException {
        sendCommand(new byte[]{ESC, INITIALIZE_PRINTER});
        // Wait for initialization
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Clear printer buffer
     */
    public void clear() throws IOException {
        sendCommand(new byte[]{CLEAR_PRINTER});
    }
    
    /**
     * Select receipt station
     */
    public void selectReceiptStation() throws IOException {
        sendCommand(new byte[]{SELECT_RECEIPT});
    }
    
    /**
     * Select slip station
     */
    public void selectSlipStation() throws IOException {
        sendCommand(new byte[]{SELECT_SLIP});
    }
    
    /**
     * Print text and feed line
     */
    public void printLine(String text) throws IOException {
        sendText(text);
        sendCommand(new byte[]{PRINT_AND_FEED});
    }
    
    /**
     * Feed paper n lines
     */
    public void feedPaper(int lines) throws IOException {
        byte[] command = {ESC, 0x64, (byte) lines};
        sendCommand(command);
    }
    
    /**
     * Set emphasized (bold) text
     */
    public void setEmphasized(boolean enable) throws IOException {
        byte[] command = {ESC, 0x45, (byte) (enable ? 1 : 0)};
        sendCommand(command);
    }
    
    /**
     * Set double-wide characters
     */
    public void setDoubleWide(boolean enable) throws IOException {
        if (enable) {
            sendCommand(new byte[]{0x12}); // DC2
        } else {
            sendCommand(new byte[]{0x13}); // DC3
        }
    }
    
    /**
     * Cut paper
     */
    public void cutPaper() throws IOException {
        byte[] command = {GS, 0x56, 0x00}; // Full cut
        sendCommand(command);
    }
    
    /**
     * Open cash drawer
     */
    public void openCashDrawer() throws IOException {
        byte[] command = {ESC, 0x70, 0x00, 55, 55}; // 110ms timing
        sendCommand(command);
    }
    
    /**
     * Simple test method
     */
    public void performSimpleTest() throws IOException {
        System.out.println("Performing simple printer test...");
        
        initialize();
        selectReceiptStation();
        
        printLine("=== NCR 7167 SIMPLE TEST ===");
        printLine("");
        printLine("Date: " + java.time.LocalDateTime.now().toString());
        printLine("Port: " + portName);
        printLine("");
        
        setEmphasized(true);
        printLine("BOLD TEXT");
        setEmphasized(false);
        
        setDoubleWide(true);
        printLine("DOUBLE WIDE");
        setDoubleWide(false);
        
        printLine("");
        printLine("Test completed!");
        
        feedPaper(3);
        
        System.out.println("Test completed successfully!");
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Ncr7167PrinterStandalone <COM_PORT>");
            System.out.println("Example: java Ncr7167PrinterStandalone COM1");
            return;
        }
        
        String portName = args[0];
        Ncr7167PrinterStandalone printer = new Ncr7167PrinterStandalone(portName);
        
        try {
            printer.connect();
            printer.performSimpleTest();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            printer.disconnect();
        }
    }
}
