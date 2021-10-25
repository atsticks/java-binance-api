package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceDepositTransaction {
    private Double amount;
    private String coin;
    private String network;
    private int status;
    private String address;
    private String addressTag;
    private String txId;
    private Long insertTime;
    private int transferType;
    private String unlockConfirm;
    private String confirmTimes;
}
