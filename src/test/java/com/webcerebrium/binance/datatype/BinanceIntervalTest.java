package com.webcerebrium.binance.datatype;

import junit.framework.TestCase;

public class BinanceIntervalTest extends TestCase {

    public void testGetValue() {
        assertEquals( BinanceInterval.FIFTEEN_MIN.getValue(), "15m");
    }

    public void testTestToString() {
        assertNotNull(BinanceInterval.ONE_HOUR.toString());
    }

    public void testLookup() {
    }

    public void testValues() {
    }

    public void testValueOf() {
    }
}