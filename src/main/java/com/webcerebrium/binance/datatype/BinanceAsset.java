package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


import java.util.Objects;

@Data
@RequiredArgsConstructor
public final class BinanceAsset {
    @NonNull
    private String name;
    private Double free;
    private Double locked;

    public void read(JsonObject ob){
        free = ob.get("free").getAsDouble();
        locked =  ob.get("locked").getAsDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinanceAsset asset = (BinanceAsset) o;
        return name.equals(asset.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}