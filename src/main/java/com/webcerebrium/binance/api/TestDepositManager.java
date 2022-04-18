package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class TestDepositManager {

    private final TestAccountManager testAccountManager;
    private List<FiatOrder> fiatOrders = new ArrayList<>();
    private List<WithdrawTransaction> fiatWithdraws = new ArrayList<>();
    private List<FiatPayment> fiatPayments = new ArrayList<>();

    private DefaultApi defaultApi;

    public TestDepositManager(TestAccountManager testAccountManager) {
        this.testAccountManager = Objects.requireNonNull(testAccountManager);
    }

    public List<FiatPayment> getFiatPayments(FiatOrderRequest request) {
        Stream<FiatPayment> stream = fiatPayments.stream();
        if(request.getBeginTime()!=null){
            stream.filter(b -> b.getCreateTime() >= request.getBeginTime());
        }
        if(request.getEndTime()!=null){
            stream.filter(b -> b.getCreateTime() < request.getEndTime());
        }
        return stream.collect(Collectors.toList());
    }

    public String withdraw(WithdrawOrder withdrawOrder) {
        WithdrawTransaction ta = new WithdrawTransaction();
        ta.setAddress(withdrawOrder.getAddress());
        ta.setAmount(withdrawOrder.getAmount());
        ta.setCoin(withdrawOrder.getCoin());
        ta.setNetwork(withdrawOrder.getNetwork());
        ta.setWithdrawOrderId(withdrawOrder.getWithdrawOrderId());
        ta.setTxId(UUID.randomUUID().toString());
        ta.setStatus(0);
        ta.setConfirmNo(0);
        ta.setTransactionFee(0.001);
        ta.setTransferType(0);
        ta.setApplyTime("immedeate");
        testAccountManager.adaptBalance(withdrawOrder);
        fiatWithdraws.add(ta);
        return ta.getId();
    }

    public List<WithdrawTransaction> getWithdrawHistory(HistoryFilter historyFilter) {
        log.warn("getWithdrawHistory not supported in simulator.");
        return Collections.emptyList();
    }

    public List<DepositTransaction> getDepositHistory(HistoryFilter historyFilter) {
        log.warn("getWithdrawHistory not supported in simulator.");
        return Collections.emptyList();
    }

    public List<FiatOrder> getFiatOrders(FiatOrderRequest request) {
        return this.fiatOrders;
    }

}
