package com.ncr.printer;

/**
 * Exception class for NCR 7167 printer-related errors
 */
public class Ncr7167Exception extends Exception {
    
    public Ncr7167Exception(String message) {
        super(message);
    }
    
    public Ncr7167Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
