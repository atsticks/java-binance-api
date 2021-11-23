package com.webcerebrium.binance.datatype;

import com.webcerebrium.binance.api.BinanceApiException;
import junit.framework.TestCase;

public class BinanceSymbolTest extends TestCase {

    public void testTestToString() throws BinanceApiException {
        BinanceSymbol sym = BinanceSymbol.valueOf("BNCUSDT");
        assertNotNull(sym);;
        assertEquals("BNCUSDT", sym.getSymbol());

    }

    public void testEquals() throws BinanceApiException {
        BinanceSymbol sym = BinanceSymbol.valueOf("BNCUSDT");
        assertNotNull(sym);;
        assertEquals("BNCUSDT", sym.getSymbol());
    }

    public void testHashCode() throws BinanceApiException {
        BinanceSymbol sym1 = BinanceSymbol.valueOf("BNCUSDT");
        BinanceSymbol sym2= BinanceSymbol.valueOf("BNCUSDT");
        BinanceSymbol sym3 = BinanceSymbol.valueOf("BNCUSDT2");
        assertEquals(sym1, sym1);
        assertEquals(sym1, sym2);
        assertNotSame(sym1, sym2);
    }
}