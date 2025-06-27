package com.ncr.printer.util;

import com.ncr.printer.Ncr7167Config;
import com.ncr.printer.Ncr7167Exception;
import com.ncr.printer.Ncr7167Printer;
import com.ncr.printer.commands.Ncr7167Commands;

/**
 * Builder class for creating formatted receipts
 */
public class ReceiptBuilder {
    
    private final StringBuilder content;
    private final Ncr7167Printer printer;
    private boolean headerSet = false;
    
    public ReceiptBuilder(Ncr7167Printer printer) {
        this.printer = printer;
        this.content = new StringBuilder();
    }
    
    /**
     * Add header with store information
     */
    public ReceiptBuilder header(String storeName, String address) throws Ncr7167Exception {
        printer.setDoubleWide(true);
        printer.printLine(center(storeName, 22)); // 22 chars for double-wide on 44 char line
        printer.setDoubleWide(false);
        
        if (address != null && !address.isEmpty()) {
            printer.printLine(center(address, 44));
        }
        
        printer.printLine(repeatChar('=', 44));
        headerSet = true;
        return this;
    }
    
    /**
     * Add a centered line
     */
    public ReceiptBuilder centerLine(String text) throws Ncr7167Exception {
        printer.printLine(center(text, 44));
        return this;
    }
    
    /**
     * Add a regular line
     */
    public ReceiptBuilder line(String text) throws Ncr7167Exception {
        printer.printLine(text);
        return this;
    }
    
    /**
     * Add an empty line
     */
    public ReceiptBuilder emptyLine() throws Ncr7167Exception {
        printer.printLine("");
        return this;
    }
    
    /**
     * Add a separator line
     */
    public ReceiptBuilder separator() throws Ncr7167Exception {
        printer.printLine(repeatChar('-', 44));
        return this;
    }
    
    /**
     * Add item line with left-aligned description and right-aligned price
     */
    public ReceiptBuilder item(String description, String price) throws Ncr7167Exception {
        String line = formatItemLine(description, price, 44);
        printer.printLine(line);
        return this;
    }
    
    /**
     * Add emphasized text
     */
    public ReceiptBuilder emphasize(String text) throws Ncr7167Exception {
        printer.setEmphasized(true);
        printer.printLine(text);
        printer.setEmphasized(false);
        return this;
    }
    
    /**
     * Add underlined text
     */
    public ReceiptBuilder underline(String text) throws Ncr7167Exception {
        printer.setUnderline(true);
        printer.printLine(text);
        printer.setUnderline(false);
        return this;
    }
    
    /**
     * Add double-wide text
     */
    public ReceiptBuilder doubleWide(String text) throws Ncr7167Exception {
        printer.setDoubleWide(true);
        printer.printLine(center(text, 22)); // 22 chars for double-wide
        printer.setDoubleWide(false);
        return this;
    }
    
    /**
     * Add bar code
     */
    public ReceiptBuilder barCode(int type, String data) throws Ncr7167Exception {
        printer.printBarCode(type, data);
        printer.printLine(""); // Add line after bar code
        return this;
    }
    
    /**
     * Add total line
     */
    public ReceiptBuilder total(String label, String amount) throws Ncr7167Exception {
        printer.printLine(repeatChar('=', 44));
        printer.setEmphasized(true);
        String totalLine = formatItemLine(label, amount, 44);
        printer.printLine(totalLine);
        printer.setEmphasized(false);
        return this;
    }
    
    /**
     * Add footer with date/time and transaction info
     */
    public ReceiptBuilder footer(String transactionId, String dateTime) throws Ncr7167Exception {
        printer.printLine(repeatChar('=', 44));
        printer.printLine(center("THANK YOU!", 44));
        
        if (transactionId != null) {
            printer.printLine("Trans ID: " + transactionId);
        }
        
        if (dateTime != null) {
            printer.printLine("Date/Time: " + dateTime);
        }
        
        return this;
    }
    
    /**
     * Feed paper and cut
     */
    public ReceiptBuilder complete() throws Ncr7167Exception {
        printer.feedPaper(3);
        
        try {
            printer.cutPaper();
        } catch (Exception e) {
            // Cutting might not be available on all configurations
        }
        
        return this;
    }
    
    // Utility methods
    
    private String center(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        
        int padding = (width - text.length()) / 2;
        return repeatChar(' ', padding) + text;
    }
    
    private String repeatChar(char c, int count) {
        return new String(new char[count]).replace('\0', c);
    }
    
    private String formatItemLine(String description, String price, int width) {
        if (description.length() + price.length() >= width) {
            // Truncate description if too long
            int maxDescLength = width - price.length() - 1;
            description = description.substring(0, Math.max(0, maxDescLength));
        }
        
        int spaces = width - description.length() - price.length();
        return description + repeatChar(' ', spaces) + price;
    }
}
