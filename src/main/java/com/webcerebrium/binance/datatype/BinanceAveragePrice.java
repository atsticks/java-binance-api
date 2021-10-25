package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Data
@RequiredArgsConstructor
public final class BinanceAveragePrice {
    @NonNull
    private String name;
    private Double price;
    private int mins;

    public void read(JsonObject ob){
        price = ob.get("price").getAsDouble();
        mins =  ob.get("mins").getAsInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinanceAveragePrice asset = (BinanceAveragePrice) o;
        return name.equals(asset.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}