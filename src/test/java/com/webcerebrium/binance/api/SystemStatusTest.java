package com.webcerebrium.binance.api;


import com.google.gson.JsonObject;
import com.webcerebrium.binance.datatype.BinanceSystemStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class SystemStatusTest {
    private BinanceApi binanceApi = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApiDefault();
    }

    @Test
    public void testSystemStatus() throws Exception, BinanceApiException {
        BinanceSystemStatus status = binanceApi.getSystemStatus();
        log.info("Status {}", status.toString() );
    }
}
