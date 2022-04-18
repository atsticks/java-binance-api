package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

// This class contains tests for trading. Take it wisely

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class TradingTest {
    private Api binanceApi = null;
    private String symbol = null;
    private Order order = null;
    private String asset = "";

    private boolean canTrade = false;
    private Asset walletAsset = null;

    @Before
    public void setUp() throws ApiException {
        binanceApi = new DefaultApi();
        asset = "BNB";
        symbol = asset + "BTC";
        order = null;

        walletAsset = binanceApi.getAccount().getAssets().get(asset);
        log.info("walletAsset={}", walletAsset.toString());
        canTrade = (walletAsset.getFree().compareTo(0d) > 0);
    }

    @After
    public void tearDown() throws Exception {
        if (order != null) {
            try {
                Order order = binanceApi.deleteOrder(this.order);
                log.info("Deleted order = {}", order.toString());
            } catch (ApiException e) {
                log.info("Order clean up (non-critical) exception = {}", e.toString());
            }
            order = null;
        }
    }

    @Test
    public void testGetTradeFee() throws ApiException {
        TradeFee tradeFee = binanceApi.getTradeFee("BTCUSDT", null);
        assertNotNull(tradeFee);
        log.info("Trade Fee = {}", tradeFee);
    }

    @Test
    public void testGetIsolatedPairs() throws ApiException {
        List<MarketPair> pairs = binanceApi.getIsolatedPairs();
        assertNotNull(pairs);
        log.info("Isolated trade pairs = {}", pairs);
    }

    @Test
    public void testGetIsolatedPairsWithWindow1000() throws ApiException {
        List<MarketPair> pairs = binanceApi.getIsolatedPairs(1000);
        assertNotNull(pairs);
        log.info("Isolated trade pairs = {}", pairs);
    }

    @Test
    public void testGetCrossMarginPairs() throws ApiException {
        List<MarketPair> pairs = binanceApi.getCrossMargingPairs();
        assertNotNull(pairs);
        log.info("Cross Margin trade pairs = {}", pairs);
    }

    @Test
    public void testOrderWithoutPlacing() throws ApiException {
        if (canTrade) {
            OrderPlacement placement = new OrderPlacement(symbol, OrderSide.SELL);
            placement.setTimeInForce(TimeInForce.GTC);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue()); // so we could tes ton BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Order Test = {}", binanceApi.createTestOrder(placement));
            }
        }
    }

    @Test
    public void testMarketOrder() throws ApiException {
        if (canTrade) {
            // Testing Buying BNB with BTC - using market price
            OrderPlacement placement = new OrderPlacement(symbol, OrderSide.BUY);
            placement.setType(OrderType.MARKET);
            Double qty = 1.0; // so we want to buy exactly 1 BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Market Order Test = {}", binanceApi.createTestOrder(placement));
            }
        }
    }

    @Test
    public void testPlacingCheckingLimitOrder() throws Exception, ApiException {
        if (canTrade) {
            OrderPlacement placement = new OrderPlacement(symbol, OrderSide.SELL);
            placement.setTimeInForce(TimeInForce.GTC);
            placement.setType(OrderType.LIMIT);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue());
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some of our asset for 1 BTC each
                OrderRef orderRef = binanceApi.createOrder(placement);
                log.info("Order Placement = {}", orderRef.toString());
                order = binanceApi.getOrder(OrderRequest.builder()
                        .orderId(orderRef.getOrderId())
                        .symbol(symbol).build());
                System.out.println(order);
            }
        }
    }


}
