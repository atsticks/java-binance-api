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
    private BinanceApi binanceApi = null;
    private String symbol = null;
    private BinanceOrder order = null;
    private String asset = "";

    private boolean canTrade = false;
    private BinanceAsset walletAsset = null;

    @Before
    public void setUp() throws BinanceApiException {
        binanceApi = new BinanceApiDefault();
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
                BinanceOrder order = binanceApi.deleteOrder(this.order);
                log.info("Deleted order = {}", order.toString());
            } catch (BinanceApiException e) {
                log.info("Order clean up (non-critical) exception = {}", e.toString());
            }
            order = null;
        }
    }

    @Test
    public void testGetTradeFee() throws BinanceApiException {
        BinanceTradeFee tradeFee = binanceApi.getTradeFee("BTCUSDT", null);
        assertNotNull(tradeFee);
        log.info("Trade Fee = {}", tradeFee);
    }

    @Test
    public void testGetIsolatedPairs() throws BinanceApiException {
        List<BinancePair> pairs = binanceApi.getIsolatedPairs();
        assertNotNull(pairs);
        log.info("Isolated trade pairs = {}", pairs);
    }

    @Test
    public void testGetIsolatedPairsWithWindow1000() throws BinanceApiException {
        List<BinancePair> pairs = binanceApi.getIsolatedPairs(1000);
        assertNotNull(pairs);
        log.info("Isolated trade pairs = {}", pairs);
    }

    @Test
    public void testGetCrossMarginPairs() throws BinanceApiException {
        List<BinancePair> pairs = binanceApi.getCrossMargingPairs();
        assertNotNull(pairs);
        log.info("Cross Margin trade pairs = {}", pairs);
    }

    @Test
    public void testOrderWithoutPlacing() throws BinanceApiException {
        if (canTrade) {
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.SELL);
            placement.setTimeInForce(BinanceTimeInForce.GTC);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue()); // so we could tes ton BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Order Test = {}", binanceApi.createTestOrder(placement));
            }
        }
    }

    @Test
    public void testMarketOrder() throws BinanceApiException {
        if (canTrade) {
            // Testing Buying BNB with BTC - using market price
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.BUY);
            placement.setType(BinanceOrderType.MARKET);
            Double qty = 1.0; // so we want to buy exactly 1 BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Market Order Test = {}", binanceApi.createTestOrder(placement));
            }
        }
    }

    @Test
    public void testPlacingCheckingLimitOrder() throws Exception, BinanceApiException {
        if (canTrade) {
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.SELL);
            placement.setTimeInForce(BinanceTimeInForce.GTC);
            placement.setType(BinanceOrderType.LIMIT);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue());
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some of our asset for 1 BTC each
                BinanceOrderRef orderRef = binanceApi.createOrder(placement);
                log.info("Order Placement = {}", orderRef.toString());
                order = binanceApi.getOrder(BinanceOrderRequest.builder()
                        .orderId(orderRef.getOrderId())
                        .symbol(symbol).build());
                System.out.println(order);
            }
        }
    }


}
