package com.webcerebrium.binance.api;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

// This class contains READ-only tests for account

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class AccountInfoTest {

    private Api binanceApi = null;
    private String symbol = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
        symbol = "BNBBTC";
    }

    @Test
    public void testAccountInformation() throws Exception, ApiException {
        Account account = binanceApi.getAccount();
        assertNotNull("account info should contain makerCommission", account.getMakerCommission());
        assertNotNull("account info should contain takerCommission", account.getTakerCommission());
        assertNotNull("account info should contain buyerCommission", account.getBuyerCommission());
        assertNotNull("account info should contain sellerCommission", account.getSellerCommission());
        assertNotNull("account info should contain canTrade", account.isCanTrade());
        assertNotNull("account info should contain canWithdraw", account.isCanWithdraw());
        assertNotNull("account info should contain canDeposit", account.isCanDeposit());
        assertNotNull("account info should contain balances", account.getAssets());
    }

    @Test
    public void testBalances() throws Exception, ApiException {
        Map<String, Asset> balances = binanceApi.getAccount().getAssets();
        assertTrue("Balances as JSON array should not be empty", balances.size() > 0);
    }

    @Test
    public void testBalancesMap() throws Exception, ApiException {
        Map<String, Asset> mapWallets = binanceApi.getAccount().getAssets();
        assertTrue("BinanceWalletAsset map should not be empty", mapWallets.size() > 0);
        log.info("Wallets={}", mapWallets.toString());

        Asset ethWallet = mapWallets.get("ETH");
        log.info("ETH Wallet={}", ethWallet.toString());
    }

    @Test
    public void testOpenOrders() throws Exception, ApiException {
        List<Order> openOrders = binanceApi.getOpenOrders(OpenOrderRequest.builder().symbol(symbol)
                .build());
        if (openOrders.size() > 0) {
            Order lastOrder = openOrders.get(0);
            log.info("last Open Order={}", lastOrder.toString());
        }
    }

    @Test
    public void testAllOrders() throws Exception, ApiException {
        List<Order> allOrders = binanceApi.getOrders(symbol);
        if (allOrders.size() > 0) {
            Order lastOrder = allOrders.get(0);
            log.info("lastOrder={}", lastOrder.toString());
        }
    }

    @Test
    public void testMyTrades() throws Exception, ApiException {
        List<Trade> binanceTrades = binanceApi.getTrades(symbol);
        if (binanceTrades.size() > 0) {
            Trade lastTrade = binanceTrades.get(0);
            log.info("lastTrade={}", lastTrade.toString());
            assertTrue("My Trades list should not be empty", binanceTrades.size() > 0);
        }
    }}
