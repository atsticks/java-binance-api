package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.events.CandlestickEvent;
import com.webcerebrium.binance.datatype.Interval;
import com.webcerebrium.binance.websocket.WebSocketCandlesticksAdapter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class KlinesStreamTest {

    private Api binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
        symbol = "ETHBTC";
    }

    @Test
    public void testKlinesStreamWatcher() throws Exception, ApiException {
        Session session = binanceApi.websocketCandlesticks(symbol, Interval.ONE_MIN, new WebSocketCandlesticksAdapter() {
            @Override
            public void onMessage(CandlestickEvent message) {
                log.info(message.toString());
            }
        });
        Thread.sleep(3000);
        session.close();
    }

}
