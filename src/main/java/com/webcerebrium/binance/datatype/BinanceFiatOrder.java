package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceFiatOrder {
    /** Example: 7d76d611-0568-4f43-afb6-24cac7767365. */
    public String orderNo;
    public String fiatCurrency;
    public double indicatedAmount;
    public double amount;
    public double totalFee;
    /** Example: "BankAccount". */
    public String method;
    public String status;
    public long createTime;
    public long updateTime;

}
