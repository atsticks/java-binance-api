package com.webcerebrium.binance.api;


import com.webcerebrium.binance.datatype.SystemStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class SystemStatusTest {
    private Api binanceApi = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
    }

    @Test
    public void testSystemStatus() throws Exception, ApiException {
        SystemStatus status = binanceApi.getSystemStatus();
        log.info("Status {}", status.toString() );
    }
}
