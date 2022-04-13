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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TestOrderManager {

    private List<Order> orders = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();

    private AtomicLong nextOrderId = new AtomicLong(System.currentTimeMillis());
    private TestAccountManager testAccountManager;

    public TestOrderManager(TestAccountManager testAccountManager) {
        this.testAccountManager = Objects.requireNonNull(testAccountManager);
    }

    public Order getOrder(OrderRef orderRef) {
        return orders.stream().filter(o -> orderRef.getOrderId()==o.getOrderId()).findFirst().orElse(null);
    }

    public Order getOrder(OrderRequest request) throws ApiException {
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

    private Order getOrderById(Long orderId) {
        return orders.stream().filter(o -> orderId == o.getOrderId()).findFirst().orElse(null);
    }

    private Order getOrderByClientOrdertId(String clientOrderId) {
        return orders.stream().filter(o -> clientOrderId.equals(o.getClientOrderId())).findFirst().orElse(null);
    }

    public OrderRef createOrder(OrderPlacement orderPlacement) throws ApiException {
        Order order = createOrderInternal(orderPlacement, false);
        this.orders.add(order);
        OrderRef ref = new OrderRef(order);
        ref.setPlacement(orderPlacement);
        return ref;
    }

    public OrderRef createTestOrder(OrderPlacement orderPlacement) throws ApiException {
        Order order = createOrderInternal(orderPlacement, true);
        this.orders.add(order);
        OrderRef ref = new OrderRef(order);
        ref.setPlacement(orderPlacement);
        return ref;
    }

    private Order createOrderInternal(OrderPlacement orderPlacement, boolean test) {
        Order order = new Order();
        order.setOrderId(nextOrderId.incrementAndGet());
        order.setClientOrderId(orderPlacement.getNewClientOrderId());
        order.setPrice(orderPlacement.getPrice());
        order.setOrigQty(orderPlacement.getQuantity());
        order.setSide(orderPlacement.getSide());
        order.setStopPrice(orderPlacement.getStopPrice());
        order.setTimeInForce(orderPlacement.getTimeInForce());
        order.setType(orderPlacement.getType());
        order.setSymbol(orderPlacement.getSymbol());
        order.setStatus(OrderStatus.NEW);
        if(test){
            order.setTest(true);
        }else {
            switch(order.getType()){
                case MARKET:
                    Trade trade = testAccountManager.adaptBalance(order);
                    if(trade!=null) {
                        this.trades.add(trade);
                    }
                    break;
                case LIMIT:
                case STOP_LOSS:
                case LIMIT_MAKER:
                case TAKE_PROFIT:
                    // handle orders as open/pending orders
                    break;
                case STOP_LOSS_LIMIT:
                case TAKE_PROFIT_LIMIT:
                default:
                    order.setStatus(OrderStatus.REJECTED);
                    break;
            }
        }
        return order;
    }

    public Order deleteOrderById(String symbol, Long orderId) throws ApiException {
        Order order = getOrderById(orderId);
        if(order!=null){
            if(!order.getSymbol().equals(symbol)){
                throw new IllegalArgumentException("Invalid symbol");
            }
            if(order.getStatus()== OrderStatus.NEW)
                order.setStatus(OrderStatus.CANCELED);
            else
                throw new ApiException("Order is not pending: "+orderId);
            return order;
        }
        return null;
    }

    public Order deleteOrderByClientOrderId(String symbol, String clientOrderId) throws ApiException {
        Order order = getOrderByClientOrdertId(clientOrderId);
        if(order!=null){
            if(!order.getSymbol().equals(symbol)){
                throw new IllegalArgumentException("Invalid symbol");
            }
            if(order.getStatus()== OrderStatus.NEW)
                order.setStatus(OrderStatus.CANCELED);
            else
                throw new ApiException("Order is not pending (client order): "+clientOrderId);
            return order;
        }
        return  null;
    }

    public List<Order> getOpenOrders() throws ApiException {
        return orders.stream().filter(o -> o.getStatus()== OrderStatus.NEW).collect(Collectors.toList());
    }

    public List<Order> getOpenOrders(OpenOrderRequest request) throws ApiException {
        Stream<Order> stream = orders.stream().filter(r -> r.getStatus()== OrderStatus.NEW);
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

    public List<Order> cancelOpenOrder(DeleteOrderRequest request) throws ApiException {
        return Collections.emptyList();
    }

    public List<Order> getOrders(AllOrderRequest request) throws ApiException {
        Stream<Order> stream = orders.stream().filter(r -> r.getStatus()== OrderStatus.NEW);
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

    public List<Order> geClosedOrders(ClosedOrderRequest request) throws ApiException {
        return orders.stream().filter(p -> p.getStatus()!= OrderStatus.NEW).collect(Collectors.toList());
    }

    public List<Order> getOrders(String symbol, Long orderId, int limit) throws ApiException {
        Stream<Order> stream = orders.stream().filter(r -> r.getStatus()== OrderStatus.NEW);
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

    public List<Trade> getMyTrades(TradesRequest request) throws ApiException {
        Stream<Trade> tradeStream = trades.stream();
        if(request.getOrderId()!=null){
            tradeStream.filter(t -> t.getId().equals(request.getOrderId()));
        }
        if(request.getSymbol()!=null){
            tradeStream.filter(t -> request.getSymbol().contains(t.getCommissionAsset()));
        }
        if(request.getStartTime()!=null){
            tradeStream.filter(t -> t.getTime()>=request.getStartTime());
        }
        if(request.getEndTime()!=null){
            tradeStream.filter(t -> t.getTime()<request.getEndTime());
        }
        if(request.getFromId()!=null){
            tradeStream.filter(t -> t.getId()>=(request.getFromId()));
        }
        return tradeStream.collect(Collectors.toList());
    }

    public List<Trade> getTrades(String symbol, int limit) throws ApiException {
        Stream<Trade> tradeStream = trades.stream();
        tradeStream.filter(t -> symbol.contains(t.getCommissionAsset()));
        return tradeStream.limit(limit).collect(Collectors.toList());
    }

    public List<AggregatedTrades> getAggregatedTrades(AggregatedTradesRequest request) {
        log.warn("getAggregatedTrades not supported by test OrderManager");
        return Collections.emptyList();
    }

    public List<HistoricalTrade> getHistoricalTrades(HistoricalTradesRequest request) {
        log.warn("getAggregatedTrades not supported by test OrderManager");
        return Collections.emptyList();
    }


}
