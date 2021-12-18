/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.collections.set.UnmodifiableSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(of = {"updateTime", "accountType"})
public class BinanceAccount {

    private Double makerCommission;
    private Double takerCommission;
    private Double buyerCommission;
    private Double sellerCommission;
    private boolean canTrade;
    private boolean canWithdraw;
    private boolean canDeposit;
    private String accountType;
    private Long updateTime;

    private Map<String, BinanceAsset> assets = new ConcurrentHashMap<>();
    private Set<String> permissions = new HashSet<>();

    public void read(JsonObject account)throws BinanceApiException {
        makerCommission = account.get("makerCommission").getAsDouble();
        takerCommission = account.get("takerCommission").getAsDouble();
        buyerCommission = account.get("buyerCommission").getAsDouble();
        sellerCommission = account.get("sellerCommission").getAsDouble();
        canTrade = account.get("canTrade").getAsBoolean();
        canWithdraw = account.get("canWithdraw").getAsBoolean();
        canDeposit = account.get("canDeposit").getAsBoolean();
        accountType = account.get("accountType").getAsString();
        updateTime = account.get("updateTime").getAsLong();
        permissions.clear();
        account.get("permissions").getAsJsonArray().forEach(p->this.permissions.add(p.getAsString()));
        readAssets(account);
    }

    private void readAssets(JsonObject account)throws BinanceApiException {
        this.assets.clear();
        account.get("balances").getAsJsonArray().forEach(b -> {
            JsonObject ob = b.getAsJsonObject();
            BinanceAsset asset = new BinanceAsset(ob.get("asset").getAsString());
            asset.read(ob);
            assets.put(asset.getName(), asset);
        });
    }

    public Set<String> getPermissions() {
        return UnmodifiableSet.decorate(permissions);
    }

    public Map<String,BinanceAsset> getAssets(){
        return UnmodifiableMap.decorate(assets);
    }

    public BinanceAsset getAsset(String symbol){
        return this.assets.get(symbol);
    }

    public Set<BinanceAsset> getNonZeroAssets(){
        return assets.values().stream()
                .filter(a -> a.getFree().doubleValue()>0d)
                .collect(Collectors.toSet());
    }

    public Set<BinanceAsset> getLockedAssets(){
        return assets.values().stream()
                .filter(a -> a.getLocked().doubleValue()>0d)
                .collect(Collectors.toSet());
    }

}
