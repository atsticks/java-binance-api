package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class TestDepositManager {

    private final TestAccountManager testAccountManager;
    private List<BinanceFiatOrder> fiatOrders = new ArrayList<>();
    private List<BinanceWithdrawTransaction> fiatWithdraws = new ArrayList<>();
    private List<BinanceFiatPayment> fiatPayments = new ArrayList<>();

    private BinanceApiDefault defaultApi;

    public TestDepositManager(TestAccountManager testAccountManager) {
        this.testAccountManager = Objects.requireNonNull(testAccountManager);
    }

    public void initDeposits(){
//        fiatOrders.addAll(defaultApi.getFiatOrders(BinanceFiatOrderRequest.builder()
//                .rows(50)
//                .build()));
//        fiatPayments.addAll(defaultApi.getFiatPayments(BinanceFiatOrderRequest.builder()
//                .rows(50)
//                .build()));
    }

    public List<BinanceFiatPayment> getFiatPayments(BinanceFiatOrderRequest request) {
        Stream<BinanceFiatPayment> stream = fiatPayments.stream();
        if(request.getBeginTime()!=null){
            stream.filter(b -> b.getCreateTime() >= request.getBeginTime());
        }
        if(request.getEndTime()!=null){
            stream.filter(b -> b.getCreateTime() < request.getEndTime());
        }
        return stream.collect(Collectors.toList());
    }

    public String withdraw(BinanceWithdrawOrder withdrawOrder) {
        BinanceWithdrawTransaction ta = new BinanceWithdrawTransaction();
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

    public List<BinanceWithdrawTransaction> getWithdrawHistory(BinanceHistoryFilter historyFilter) {
        log.warn("getWithdrawHistory not supported in simulator.");
        return Collections.emptyList();
    }

    public List<BinanceDepositTransaction> getDepositHistory(BinanceHistoryFilter historyFilter) {
        log.warn("getWithdrawHistory not supported in simulator.");
        return Collections.emptyList();
    }

    public List<BinanceFiatOrder> getFiatOrders(BinanceFiatOrderRequest request) {
        return this.fiatOrders;
    }

}
