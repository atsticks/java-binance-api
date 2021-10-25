package com.webcerebrium.binance.datatype;

import lombok.*;


import java.util.Objects;

@Data
@RequiredArgsConstructor
public final class BinancePrice {
    @NonNull
    private String symbol;
    private Double value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinancePrice price = (BinancePrice) o;
        return symbol.equals(price.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

}