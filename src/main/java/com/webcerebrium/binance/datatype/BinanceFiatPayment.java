package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceFiatPayment {
    /** Example: 7d76d611-0568-4f43-afb6-24cac7767365. */
    public String orderNo;
    public double sourceAmount;
    public String fiatCurrency;
    public double obtainAmount;
    public String cryptoCurrency;
    public double totalFee;
    public double price;
    public String status;
    public long createTime;
    public long updateTime;

}
