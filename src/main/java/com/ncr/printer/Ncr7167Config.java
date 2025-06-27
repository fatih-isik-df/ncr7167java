package com.ncr.printer;

/**
 * Configuration class for NCR 7167 printer communication
 */
public class Ncr7167Config {
    
    // Default RS-232C settings based on the manual
    public static final int DEFAULT_BAUD_RATE = 9600;
    public static final int DEFAULT_DATA_BITS = 8;
    public static final int DEFAULT_STOP_BITS = 1;
    public static final int DEFAULT_PARITY = 0; // No parity
    public static final int DEFAULT_TIMEOUT = 5000; // 5 seconds
    
    // Flow control types
    public enum FlowControl {
        NONE,
        XON_XOFF,
        DTR_DSR
    }
    
    // Communication interface types
    public enum InterfaceType {
        RS232C,
        USB
    }
    
    // Station selection
    public enum Station {
        RECEIPT,
        SLIP
    }
    
    private String portName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int timeout;
    private FlowControl flowControl;
    private InterfaceType interfaceType;
    
    public Ncr7167Config() {
        this.baudRate = DEFAULT_BAUD_RATE;
        this.dataBits = DEFAULT_DATA_BITS;
        this.stopBits = DEFAULT_STOP_BITS;
        this.parity = DEFAULT_PARITY;
        this.timeout = DEFAULT_TIMEOUT;
        this.flowControl = FlowControl.XON_XOFF;
        this.interfaceType = InterfaceType.RS232C;
    }
    
    // Getters and setters
    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }
    
    public int getBaudRate() { return baudRate; }
    public void setBaudRate(int baudRate) { this.baudRate = baudRate; }
    
    public int getDataBits() { return dataBits; }
    public void setDataBits(int dataBits) { this.dataBits = dataBits; }
    
    public int getStopBits() { return stopBits; }
    public void setStopBits(int stopBits) { this.stopBits = stopBits; }
    
    public int getParity() { return parity; }
    public void setParity(int parity) { this.parity = parity; }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public FlowControl getFlowControl() { return flowControl; }
    public void setFlowControl(FlowControl flowControl) { this.flowControl = flowControl; }
    
    public InterfaceType getInterfaceType() { return interfaceType; }
    public void setInterfaceType(InterfaceType interfaceType) { this.interfaceType = interfaceType; }
}
