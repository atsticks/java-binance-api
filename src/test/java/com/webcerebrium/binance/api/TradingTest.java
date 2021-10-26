package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

// This class contains tests for trading. Take it wisely

import com.google.gson.JsonObject;
import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

@Slf4j
public class TradingTest {
    private BinanceApi binanceApi = null;
    private BinanceSymbol symbol = null;
    private BinanceOrder order = null;
    private String asset = "";

    private boolean canTrade = false;
    private BinanceAsset walletAsset = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApi();
        asset = "BNB";
        symbol = BinanceSymbol.valueOf(asset + "BTC");
        order = null;

        walletAsset = binanceApi.getAccount().getAssets().get(asset);
        log.info("walletAsset={}", walletAsset.toString());
        canTrade = (walletAsset.getFree().compareTo(0d) > 0);
    }

    @After
    public void tearDown() throws Exception {
        if (order != null) {
            try {
                BinanceDeletedOrder jsonObject = binanceApi.deleteOrder(order);
                log.info("Deleted order = {}", jsonObject.toString());
            } catch (BinanceApiException e) {
                log.info("Order clean up (non-critical) exception = {}", e.toString());
            }
            order = null;
        }
    }

    @Test
    public void testOrderWithoutPlacing() throws Exception, BinanceApiException {
        if (canTrade) {
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.SELL);
            placement.setTimeInForce(BinanceTimeInForce.GOOD_TILL_CANCELLED);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue()); // so we could tes ton BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Order Test = {}", binanceApi.testOrder(placement));
            }
        }
    }

    @Test
    public void testMarketOrder() throws Exception, BinanceApiException {
        if (canTrade) {
            // Testing Buying BNB with BTC - using market price
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.BUY);
            placement.setType(BinanceOrderType.MARKET);
            Double qty = 1.0; // so we want to buy exactly 1 BNB
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some our asset for 1 BTC each
                log.info("Market Order Test = {}", binanceApi.testOrder(placement));
            }
        }
    }

    @Test
    public void testPlacingCheckingLimitOrder() throws Exception, BinanceApiException {
        if (canTrade) {
            BinanceOrderPlacement placement = new BinanceOrderPlacement(symbol, BinanceOrderSide.SELL);
            placement.setTimeInForce(BinanceTimeInForce.GOOD_TILL_CANCELLED);
            placement.setType(BinanceOrderType.LIMIT);
            placement.setPrice(1d);

            Double qty = Double.valueOf(walletAsset.getFree().longValue());
            if (qty.compareTo(0d) > 0) {
                placement.setQuantity(qty); // sell some of our asset for 1 BTC each
                BinanceNewOrder jsonObject = binanceApi.createOrder(placement);
                log.info("Order Placement = {}", jsonObject.toString());
                order = binanceApi.getOrderById(symbol, jsonObject.getOrderId());
                System.out.println(order);
            }
        }
    }


}
