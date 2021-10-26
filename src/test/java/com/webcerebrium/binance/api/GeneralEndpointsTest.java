package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class GeneralEndpointsTest {

    private BinanceApi binanceApi = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApi();
    }

    @Test
    public void testPingReturnsEmptyObject() throws Exception, BinanceApiException {
        binanceApi.ping();
    }

    @Test
    public void testServerTimeIsAlmostSameAsLocal() throws Exception, BinanceApiException {
        Long time = binanceApi.getServerTime();
        assertNotNull("serverTime should be received", time);
        long localTime = (new Date()).getTime();
        assertTrue("serverTime should not differ much from local", Math.abs(time - localTime) < 5000);
    }
}
