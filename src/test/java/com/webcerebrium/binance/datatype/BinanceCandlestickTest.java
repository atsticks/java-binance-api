package com.webcerebrium.binance.datatype;

import junit.framework.TestCase;

public class BinanceCandlestickTest extends TestCase {

    public void testGetChangePerTimeUnit() {
        BinanceCandlestick bs = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);

    }

    public void testGetChangeRate() {
        BinanceCandlestick bs = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
    }

    public void testGetChangeRatePerTimeUnit() {
        BinanceCandlestick bs = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
    }

    public void testTestToString() {
        BinanceCandlestick bs = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
        assertNotNull(bs.toString());
    }

    public void testTestEquals() {
        BinanceCandlestick bs10 = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs11 = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs2 = new BinanceCandlestick("BTCUSDC2", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs3 = new BinanceCandlestick("BTCUSDC", BinanceInterval.ONE_HOUR);
        assertEquals(bs10, bs11);
        assertFalse(bs10.equals(bs2));
        assertFalse(bs11.equals(bs2));
        assertFalse(bs11.equals(bs3));
    }


    public void testTestHashCode() {
        BinanceCandlestick bs10 = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs11 = new BinanceCandlestick("BTCUSDC", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs2 = new BinanceCandlestick("BTCUSDC2", BinanceInterval.FIVE_MIN);
        BinanceCandlestick bs3 = new BinanceCandlestick("BTCUSDC", BinanceInterval.ONE_HOUR);
        assertEquals(bs10.hashCode(), bs11.hashCode());
        assertFalse(bs11.hashCode() == bs2.hashCode());
        assertFalse(bs11.hashCode() == bs3.hashCode());
    }

}