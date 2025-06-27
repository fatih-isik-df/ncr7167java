package com.ncr.printer.commands;

/**
 * NCR 7167 printer command constants based on the manual
 */
public class Ncr7167Commands {
    
    // Printer Function Commands
    public static final byte CLEAR_PRINTER = 0x10;
    public static final byte CLOSE_FORM = 0x11;
    public static final byte OPEN_FORM = 0x18;
    
    // Vertical Positioning and Print Commands
    public static final byte PRINT_AND_FEED_ONE_LINE = 0x0A; // LF
    public static final byte PRINT_AND_EJECT_SLIP = 0x0C; // FF
    public static final byte PRINT_AND_CARRIAGE_RETURN = 0x0D; // CR
    public static final byte FEED_N_PRINT_LINES = 0x14;
    public static final byte FEED_N_DOT_ROWS = 0x15;
    public static final byte ADD_N_EXTRA_DOT_ROWS = 0x16;
    public static final byte PRINT = 0x17;
    
    // Station Selection
    public static final byte SELECT_RECEIPT_STATION = 0x1E; // RS
    public static final byte SELECT_SLIP_STATION = 0x1C; // FS
    
    // Print Characteristic Commands
    public static final byte SELECT_DOUBLE_WIDE = 0x12;
    public static final byte SELECT_SINGLE_WIDE = 0x13;
    
    // ESC sequences
    public static final byte ESC = 0x1B;
    
    // Multi-byte commands with ESC prefix
    public static class EscCommands {
        public static final byte[] SELECT_90_DEGREE_CCW_ROTATED = {ESC, 0x12};
        public static final byte[] SELECT_PITCH = {ESC, 0x16}; // + n
        public static final byte[] SET_CHARACTER_RIGHT_SPACING = {ESC, 0x20}; // + n
        public static final byte[] SELECT_PRINT_MODES = {ESC, 0x21}; // + n
        public static final byte[] SELECT_USER_DEFINED_CHARSET = {ESC, 0x25}; // + n
        public static final byte[] DEFINE_USER_DEFINED_CHARS = {ESC, 0x26}; // + parameters
        public static final byte[] SELECT_UNDERLINE_MODE = {ESC, 0x2D}; // + n
        public static final byte[] SET_LINE_SPACING_1_6_INCH = {ESC, 0x32};
        public static final byte[] SET_LINE_SPACING = {ESC, 0x33}; // + n
        public static final byte[] INITIALIZE_PRINTER = {ESC, 0x40};
        public static final byte[] SET_SLIP_PAPER_EJECT_LENGTH = {ESC, 0x43}; // + n
        public static final byte[] SELECT_EMPHASIZED = {ESC, 0x45}; // + n
        public static final byte[] SELECT_DOUBLE_STRIKE = {ESC, 0x47}; // + n
        public static final byte[] CANCEL_EMPHASIZED = {ESC, 0x48};
        public static final byte[] SELECT_ITALIC = {ESC, 0x49}; // + n
        public static final byte[] PRINT_AND_FEED_N_DOTS = {ESC, 0x4A}; // + n
        public static final byte[] PRINT_AND_REVERSE_FEED = {ESC, 0x4B}; // + n
        public static final byte[] SELECT_PAGE_MODE = {ESC, 0x4C};
        public static final byte[] INTERNATIONAL_CHARSET = {ESC, 0x52}; // + n
        public static final byte[] SELECT_STANDARD_MODE = {ESC, 0x53};
        public static final byte[] SELECT_PRINT_DIRECTION_PAGE_MODE = {ESC, 0x54}; // + n
        public static final byte[] SELECT_UNIDIRECTIONAL = {ESC, 0x55}; // + n
        public static final byte[] SELECT_90_DEGREE_CW_ROTATED = {ESC, 0x56}; // + n
        public static final byte[] PRINT_AND_FEED_N_LINES = {ESC, 0x64}; // + n
        public static final byte[] PRINT_AND_REVERSE_FEED_N_LINES = {ESC, 0x65}; // + n
        public static final byte[] OPEN_CASH_DRAWER = {ESC, 0x70}; // + n + m
        public static final byte[] SELECT_PRINT_COLOR = {ESC, 0x72}; // + n
        public static final byte[] UPSIDE_DOWN_PRINTING = {ESC, 0x7B}; // + n
    }
    
    // GS sequences
    public static final byte GS = 0x1D;
    
    public static class GsCommands {
        public static final byte[] REVERSE_FEED_N_LINES = {GS, 0x14}; // + n
        public static final byte[] REVERSE_FEED_N_DOTS = {GS, 0x15}; // + n
        public static final byte[] SELECT_CHAR_SIZE = {GS, 0x21}; // + n
        public static final byte[] DEFINE_DOWNLOADED_BIT_IMAGE = {GS, 0x2A}; // + parameters
        public static final byte[] SET_HV_MOTION_UNITS = {GS, 0x50}; // + x + y
        public static final byte[] CUT_PAPER = {GS, 0x56}; // + m or + m + n
        public static final byte[] SET_LEFT_MARGIN = {GS, 0x4C}; // + nL + nH
        public static final byte[] SET_PRINT_AREA_WIDTH = {GS, 0x57}; // + nL + nH
        public static final byte[] PRINT_BAR_CODE = {GS, 0x6B}; // + parameters
        public static final byte[] SET_BAR_CODE_HEIGHT = {GS, 0x68}; // + n
        public static final byte[] SELECT_HRI_POSITION = {GS, 0x48}; // + n
        public static final byte[] TRANSMIT_STATUS = {GS, 0x72}; // + n
        public static final byte[] REQUEST_PRINTER_STATUS = {GS, 0x05};
        public static final byte[] REAL_TIME_STATUS = {GS, 0x04}; // + n
    }
    
    // Horizontal positioning commands
    public static final byte HORIZONTAL_TAB = 0x09;
    
    // Special characters
    public static final byte NUL = 0x00;
    public static final byte XON = 0x11;
    public static final byte XOFF = 0x13;
    
    // Status bytes for Auto Status Back (ASB)
    public static class StatusBytes {
        // Printer information byte bits
        public static final int CASH_DRAWER_OPEN = 0x04;
        public static final int RS232_BUSY = 0x08;
        public static final int RECEIPT_COVER_OPEN = 0x20;
        public static final int PAPER_FEED_BUTTON_PRESSED = 0x40;
        
        // Error information byte bits
        public static final int KNIFE_ERROR = 0x08;
        public static final int RECEIPT_PAPER_OUT = 0x20;
        public static final int RECEIPT_PAPER_LOW = 0x60;
        
        // Paper sensor information
        public static final int SLIP_PAPER_PRESENT = 0x20;
        public static final int ERROR_CONDITION = 0x40;
    }
    
    // Bar code types
    public static class BarCodeTypes {
        public static final int UPC_A = 0;
        public static final int UPC_E = 1;
        public static final int JAN13_EAN13 = 2;
        public static final int JAN8_EAN8 = 3;
        public static final int CODE39 = 4;
        public static final int ITF = 5; // Interleaved 2 of 5
        public static final int CODABAR = 6;
        public static final int CODE93 = 9;
        public static final int CODE128 = 10;
        public static final int PDF417 = 75;
    }
    
    // Print modes for SELECT_PRINT_MODES command
    public static class PrintModes {
        public static final int STANDARD_PITCH = 0x00;
        public static final int COMPRESSED_PITCH = 0x01;
        public static final int EMPHASIZED = 0x08;
        public static final int DOUBLE_HEIGHT = 0x10;
        public static final int DOUBLE_WIDTH = 0x20;
        public static final int UNDERLINE = 0x80;
    }
    
    // Character pitch values
    public static class CharacterPitch {
        public static final int STANDARD_80MM = 44;
        public static final int COMPRESSED_80MM = 56;
        public static final int STANDARD_58MM = 32;
        public static final int COMPRESSED_58MM = 42;
        public static final int STANDARD_SLIP = 45;
        public static final int COMPRESSED_SLIP = 55;
    }
}
