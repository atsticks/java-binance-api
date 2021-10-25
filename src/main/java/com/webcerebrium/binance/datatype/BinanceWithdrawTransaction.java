package com.webcerebrium.binance.datatype;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BinanceWithdrawTransaction {
    private String address;
    private Double amount;
    private String applyTime;
    private String coin;
    private String id;
    private String withdrawOrderId;
    private String network;
    private int status;
    private int transferType;
    private Double transactionFee;
    /** Confirm times for withdraw. */
    private Integer confirmNo;
    private String txId;
}
