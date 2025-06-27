package com.ncr.printer.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Ncr7167CommandsTest {
    
    @Test
    void testBasicCommands() {
        assertEquals(0x10, Ncr7167Commands.CLEAR_PRINTER);
        assertEquals(0x0A, Ncr7167Commands.PRINT_AND_FEED_ONE_LINE);
        assertEquals(0x1E, Ncr7167Commands.SELECT_RECEIPT_STATION);
        assertEquals(0x1C, Ncr7167Commands.SELECT_SLIP_STATION);
        assertEquals(0x12, Ncr7167Commands.SELECT_DOUBLE_WIDE);
        assertEquals(0x13, Ncr7167Commands.SELECT_SINGLE_WIDE);
    }
    
    @Test
    void testEscCommands() {
        assertArrayEquals(new byte[]{0x1B, 0x40}, Ncr7167Commands.EscCommands.INITIALIZE_PRINTER);
        assertArrayEquals(new byte[]{0x1B, 0x32}, Ncr7167Commands.EscCommands.SET_LINE_SPACING_1_6_INCH);
        assertArrayEquals(new byte[]{0x1B, 0x4C}, Ncr7167Commands.EscCommands.SELECT_PAGE_MODE);
    }
    
    @Test
    void testGsCommands() {
        assertArrayEquals(new byte[]{0x1D, 0x05}, Ncr7167Commands.GsCommands.REQUEST_PRINTER_STATUS);
        assertArrayEquals(new byte[]{0x1D, 0x56}, Ncr7167Commands.GsCommands.CUT_PAPER);
    }
    
    @Test
    void testBarCodeTypes() {
        assertEquals(0, Ncr7167Commands.BarCodeTypes.UPC_A);
        assertEquals(4, Ncr7167Commands.BarCodeTypes.CODE39);
        assertEquals(10, Ncr7167Commands.BarCodeTypes.CODE128);
        assertEquals(75, Ncr7167Commands.BarCodeTypes.PDF417);
    }
    
    @Test
    void testPrintModes() {
        assertEquals(0x00, Ncr7167Commands.PrintModes.STANDARD_PITCH);
        assertEquals(0x01, Ncr7167Commands.PrintModes.COMPRESSED_PITCH);
        assertEquals(0x08, Ncr7167Commands.PrintModes.EMPHASIZED);
        assertEquals(0x10, Ncr7167Commands.PrintModes.DOUBLE_HEIGHT);
        assertEquals(0x20, Ncr7167Commands.PrintModes.DOUBLE_WIDTH);
        assertEquals(0x80, Ncr7167Commands.PrintModes.UNDERLINE);
    }
    
    @Test
    void testCharacterPitch() {
        assertEquals(44, Ncr7167Commands.CharacterPitch.STANDARD_80MM);
        assertEquals(56, Ncr7167Commands.CharacterPitch.COMPRESSED_80MM);
        assertEquals(32, Ncr7167Commands.CharacterPitch.STANDARD_58MM);
        assertEquals(42, Ncr7167Commands.CharacterPitch.COMPRESSED_58MM);
        assertEquals(45, Ncr7167Commands.CharacterPitch.STANDARD_SLIP);
        assertEquals(55, Ncr7167Commands.CharacterPitch.COMPRESSED_SLIP);
    }
}
