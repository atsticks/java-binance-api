package com.webcerebrium.binance.datatype;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BinanceDepth {

    @NonNull
    String symbol;
    @NonNull
    BinanceBidOrAsk asks;
    @NonNull
    BinanceBidOrAsk bids;

}
