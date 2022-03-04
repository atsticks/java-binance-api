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

package com.webcerebrium.binance.api;

import com.webcerebrium.binance.datatype.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestOrderManager {

    private List<BinanceOrder> orders = new ArrayList<>();

    private AtomicLong nextOrderId = new AtomicLong(System.currentTimeMillis());

    public BinanceOrder getOrder(BinanceOrderRef orderRef) {
        return orders.stream().filter(o -> orderRef.getOrderId()==o.getOrderId()).findFirst().orElse(null);
    }

    public BinanceOrder getOrder(BinanceOrderRequest request) throws BinanceApiException {
        if(request.getOrderId()!=null) {
            return getOrderById(request.getOrderId());
        }
        if(request.getOrigClientOrderId()!=null){
            return getOrderByClientOrdertId(request.getOrigClientOrderId());
        }
        if(request.getSymbol()!=null){
            return orders.stream().filter(o -> request.getSymbol() == o.getSymbol()).filter(o -> request.getTimestamp() == o.getTime()).findFirst().orElse(null);
        }
        return null;
    }

    private BinanceOrder getOrderById(Long orderId) {
        return orders.stream().filter(o -> orderId == o.getOrderId()).findFirst().orElse(null);
    }

    private BinanceOrder getOrderByClientOrdertId(String clientOrderId) {
        return orders.stream().filter(o -> clientOrderId.equals(o.getClientOrderId())).findFirst().orElse(null);
    }

    public BinanceOrderRef createOrder(BinanceOrderPlacement orderPlacement) throws BinanceApiException {
        BinanceOrder order = createOrderInternal(orderPlacement);
        this.orders.add(order);
        BinanceOrderRef ref = new BinanceOrderRef(order);
        ref.setPlacement(orderPlacement);
        return ref;
    }

    public BinanceOrderRef createTestOrder(BinanceOrderPlacement orderPlacement) throws BinanceApiException {
        BinanceOrder order = createOrderInternal(orderPlacement);
        order.setTest(true);
        this.orders.add(order);
        BinanceOrderRef ref = new BinanceOrderRef(order);
        ref.setPlacement(orderPlacement);
        return ref;
    }

    private BinanceOrder createOrderInternal(BinanceOrderPlacement orderPlacement) {
        BinanceOrder order = new BinanceOrder();
        order.setOrderId(nextOrderId.incrementAndGet());
        order.setClientOrderId(orderPlacement.getNewClientOrderId());
        order.setPrice(orderPlacement.getPrice());
        order.setOrigQty(orderPlacement.getQuantity());
        order.setSide(orderPlacement.getSide());
        order.setStopPrice(orderPlacement.getStopPrice());
        order.setTime(System.currentTimeMillis());
        order.setTimeInForce(orderPlacement.getTimeInForce());
        order.setType(orderPlacement.getType());
        order.setSymbol(orderPlacement.getSymbol());
        order.setStatus(BinanceOrderStatus.NEW);
        return order;
    }

    public BinanceOrder deleteOrderById(String symbol, Long orderId) throws BinanceApiException {
        BinanceOrder order = getOrderById(orderId);
        if(order!=null){
            if(!order.getSymbol().equals(symbol)){
                throw new IllegalArgumentException("Invalid symbol");
            }
            if(order.getStatus()==BinanceOrderStatus.NEW)
                order.setStatus(BinanceOrderStatus.CANCELED);
            else
                throw new BinanceApiException("Order is not pending: "+orderId);
            return order;
        }
        return null;
    }

    public BinanceOrder deleteOrderByClientOrderId(String symbol, String clientOrderId) throws BinanceApiException {
        BinanceOrder order = getOrderByClientOrdertId(clientOrderId);
        if(order!=null){
            if(!order.getSymbol().equals(symbol)){
                throw new IllegalArgumentException("Invalid symbol");
            }
            if(order.getStatus()==BinanceOrderStatus.NEW)
                order.setStatus(BinanceOrderStatus.CANCELED);
            else
                throw new BinanceApiException("Order is not pending (client order): "+clientOrderId);
            return order;
        }
        return  null;
    }

    public List<BinanceOrder> getOpenOrders() throws BinanceApiException {
        return orders.stream().filter(o -> o.getStatus()==BinanceOrderStatus.NEW).collect(Collectors.toList());
    }

    public List<BinanceOrder> getOpenOrders(BinanceOpenOrderRequest request) throws BinanceApiException {
        Stream<BinanceOrder> stream = orders.stream().filter(r -> r.getStatus()==BinanceOrderStatus.NEW);
        if(request.getSymbol()!=null){
            stream.filter(r -> r.getSymbol().equals(request.getSymbol()));
        }
        if(request.getOffset()!=null){
            stream.skip(request.getOffset());
        }
        if(request.getLimit()!=null){
            stream.limit(request.getLimit());
        }
        return stream.collect(Collectors.toList());
    }

    public List<BinanceOrder> cancelOpenOrder(BinanceDeleteOrderRequest request) throws BinanceApiException {
        return Collections.emptyList();
    }

    public List<BinanceOrder> getOrders(BinanceAllOrderRequest request) throws BinanceApiException {
        Stream<BinanceOrder> stream = orders.stream().filter(r -> r.getStatus()==BinanceOrderStatus.NEW);
        if(request.getSymbol()!=null){
            stream.filter(r -> r.getSymbol().equals(request.getSymbol()));
        }
        if(request.getOrderId()!=null){
            stream.filter(r -> r.getOrderId().equals(request.getOrderId()));
        }
        if(request.getLimit()>0){
            stream.limit(request.getLimit());
        }
        if(request.getOrigClientOrderId()!=null){
            stream.filter(i -> i.getClientOrderId().equals(request.getOrigClientOrderId()));
        }
        return stream.collect(Collectors.toList());
    }

    public List<BinanceOrder> geClosedOrders(BinanceClosedOrderRequest request) throws BinanceApiException {
        return orders.stream().filter(p -> p.getStatus()!=BinanceOrderStatus.NEW).collect(Collectors.toList());
    }

    public List<BinanceOrder> getOrders(String symbol, Long orderId, int limit) throws BinanceApiException {
        Stream<BinanceOrder> stream = orders.stream().filter(r -> r.getStatus()==BinanceOrderStatus.NEW);
        if(symbol!=null){
            stream.filter(r -> r.getSymbol().equals(symbol));
        }
        if(orderId!=null){
            stream.filter(r -> r.getOrderId().equals(orderId));
        }
        if(limit>0)
            stream.limit(limit);
        return stream.collect(Collectors.toList());
    }

    public List<BinanceTrade> getMyTrades(BinanceMyTradesRequest request) throws BinanceApiException {
        BinanceTrade trade = new BinanceTrade();
        trade.
        return testOrderManager.getMyTrades(request);
    }

    public List<BinanceTrade> getTrades(String symbol, int limit) throws BinanceApiException {
        return testOrderManager.getTrades(symbol, limit);
    }

}
