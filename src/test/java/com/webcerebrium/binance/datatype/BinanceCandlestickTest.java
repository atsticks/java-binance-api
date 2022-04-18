package com.webcerebrium.binance.datatype;

import junit.framework.TestCase;

public class BinanceCandlestickTest extends TestCase {

    public void testGetChangePerTimeUnit() {
        Candlestick bs = new Candlestick("BTCUSDC", Interval.FIVE_MIN);

    }

    public void testGetChangeRate() {
        Candlestick bs = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
    }

    public void testGetChangeRatePerTimeUnit() {
        Candlestick bs = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
    }

    public void testTestToString() {
        Candlestick bs = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
        assertNotNull(bs.toString());
    }

    public void testTestEquals() {
        Candlestick bs10 = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
        Candlestick bs11 = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
        Candlestick bs2 = new Candlestick("BTCUSDC2", Interval.FIVE_MIN);
        Candlestick bs3 = new Candlestick("BTCUSDC", Interval.ONE_HOUR);
        assertEquals(bs10, bs11);
        assertFalse(bs10.equals(bs2));
        assertFalse(bs11.equals(bs2));
        assertFalse(bs11.equals(bs3));
    }


    public void testTestHashCode() {
        Candlestick bs10 = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
        Candlestick bs11 = new Candlestick("BTCUSDC", Interval.FIVE_MIN);
        Candlestick bs2 = new Candlestick("BTCUSDC2", Interval.FIVE_MIN);
        Candlestick bs3 = new Candlestick("BTCUSDC", Interval.ONE_HOUR);
        assertEquals(bs10.hashCode(), bs11.hashCode());
        assertFalse(bs11.hashCode() == bs2.hashCode());
        assertFalse(bs11.hashCode() == bs3.hashCode());
    }

}