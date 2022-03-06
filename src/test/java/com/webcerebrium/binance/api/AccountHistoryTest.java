package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.BinanceHistoryFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

@Slf4j
public class AccountHistoryTest {

    private BinanceApi binanceApi = null;

    @Before
    public void setUp() throws Exception, BinanceApiException {
        binanceApi = new BinanceApiDefault();
    }

    @Test
    public void testAccountDepositHistory() throws Exception, BinanceApiException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        BinanceHistoryFilter historyFilter = BinanceHistoryFilter.builder()
                .coin("ETH")
                .startTime(cal.getTime().getTime()).build();

        log.info("DEPOSIT={}", binanceApi.getDepositHistory(historyFilter));
    }

    @Test
    public void testAccountWithdrawalHistory() throws Exception, BinanceApiException {
        BinanceHistoryFilter historyFilter = BinanceHistoryFilter.builder()
                .coin("ETH").build();
        log.info("WITHDRAWALS={}", binanceApi.getWithdrawHistory(historyFilter));
    }
}
