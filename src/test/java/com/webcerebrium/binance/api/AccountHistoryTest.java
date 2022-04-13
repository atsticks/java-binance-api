package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.HistoryFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

@Slf4j
public class AccountHistoryTest {

    private Api binanceApi = null;

    @Before
    public void setUp() throws Exception, ApiException {
        binanceApi = new DefaultApi();
    }

    @Test
    public void testAccountDepositHistory() throws Exception, ApiException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        HistoryFilter historyFilter = HistoryFilter.builder()
                .coin("ETH")
                .startTime(cal.getTime().getTime()).build();

        log.info("DEPOSIT={}", binanceApi.getDepositHistory(historyFilter));
    }

    @Test
    public void testAccountWithdrawalHistory() throws Exception, ApiException {
        HistoryFilter historyFilter = HistoryFilter.builder()
                .coin("ETH").build();
        log.info("WITHDRAWALS={}", binanceApi.getWithdrawHistory(historyFilter));
    }
}
