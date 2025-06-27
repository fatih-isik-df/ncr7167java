package com.ncr.printer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class Ncr7167ConfigTest {
    
    private Ncr7167Config config;
    
    @BeforeEach
    void setUp() {
        config = new Ncr7167Config();
    }
    
    @Test
    void testDefaultValues() {
        assertEquals(9600, config.getBaudRate());
        assertEquals(8, config.getDataBits());
        assertEquals(1, config.getStopBits());
        assertEquals(0, config.getParity());
        assertEquals(5000, config.getTimeout());
        assertEquals(Ncr7167Config.FlowControl.XON_XOFF, config.getFlowControl());
        assertEquals(Ncr7167Config.InterfaceType.RS232C, config.getInterfaceType());
    }
    
    @Test
    void testSettersAndGetters() {
        config.setPortName("COM1");
        config.setBaudRate(19200);
        config.setDataBits(7);
        config.setStopBits(2);
        config.setParity(1);
        config.setTimeout(10000);
        config.setFlowControl(Ncr7167Config.FlowControl.DTR_DSR);
        config.setInterfaceType(Ncr7167Config.InterfaceType.USB);
        
        assertEquals("COM1", config.getPortName());
        assertEquals(19200, config.getBaudRate());
        assertEquals(7, config.getDataBits());
        assertEquals(2, config.getStopBits());
        assertEquals(1, config.getParity());
        assertEquals(10000, config.getTimeout());
        assertEquals(Ncr7167Config.FlowControl.DTR_DSR, config.getFlowControl());
        assertEquals(Ncr7167Config.InterfaceType.USB, config.getInterfaceType());
    }
}
