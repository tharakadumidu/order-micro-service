package com.example.order.service;

import com.example.order.api.model.OrderRequest;
import com.example.order.api.model.OrderResponse;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.OrderUpdateFailedException;
import com.example.order.repository.CoffeeShopItemPricingRepository;
import com.example.order.repository.CoffeeShopRepository;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.entity.CoffeeShop;
import com.example.order.repository.entity.CoffeeShopItemPricing;
import com.example.order.repository.entity.Order;
import com.example.order.transformer.OrderRequestResponseTransformer;
import com.example.order.util.OrderMetaData;
import com.example.order.util.OrderStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


import javax.jms.Queue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private CoffeeShopItemPricingRepository coffeeShopItemPricingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CoffeeShopRepository coffeeShopRepository;
    @Autowired
    private Queue queue;
    @Autowired
    private JmsTemplate jmsTemplate;

    public OrderResponse createOrder(final OrderRequest orderRequest) {
        final List<CoffeeShopItemPricing> coffeeShopItemPricingList =  new ArrayList<>();
        CollectionUtils.addAll(coffeeShopItemPricingList, coffeeShopItemPricingRepository.findAllById(orderRequest.getItems()));
        Order order = OrderRequestResponseTransformer.convertRequestToEntity(orderRequest, coffeeShopItemPricingList);
        final List<Order> pendingOrders = orderRepository.findByCoffeeShopId(order.getCoffeeShopId());
        final OrderMetaData orderMetaData = calculateNextQueueNumberAndDeliveryTime(pendingOrders);
        order.setQueueNumber(orderMetaData.getNextQueueNumber());
        order.setExpectedDeliveryTime(orderMetaData.getExpectedDeliveryTime());
        order = orderRepository.save(order);
        final OrderResponse orderResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(order);
        return orderResponse;
    }

    public OrderResponse updateOrder(final OrderRequest orderRequest, final Integer orderId) {
        Order previousOrder = orderRepository.findById(orderId).get();
        if(previousOrder == null){
            throw new OrderNotFoundException();
        }
        if(!previousOrder.getStatus().equals(OrderStatus.CREATED)){
            throw new OrderUpdateFailedException();
        }
        final List<CoffeeShopItemPricing> coffeeShopItemPricingList =  new ArrayList<>();
        CollectionUtils.addAll(coffeeShopItemPricingList, coffeeShopItemPricingRepository.findAllById(orderRequest.getItems()));
        previousOrder = OrderRequestResponseTransformer.updateOrderToEntity(orderRequest, previousOrder, coffeeShopItemPricingList);
        previousOrder = orderRepository.save(previousOrder);
        final OrderResponse orderUpdateResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(previousOrder);
        return orderUpdateResponse;
    }

    public void updateStatus(final OrderStatus orderStatus, final Integer orderId) {
        Order previousOrder = orderRepository.findById(orderId).get();
        if(previousOrder == null){
            throw new OrderNotFoundException();
        }
        if(orderStatus.equals(OrderStatus.CANCELED) && !previousOrder.getStatus().equals(OrderStatus.CREATED)){
            throw new OrderUpdateFailedException();
        }
        previousOrder.setStatus(orderStatus);
        if(orderStatus.equals(OrderStatus.SERVED)){
            jmsTemplate.convertAndSend(queue, previousOrder);
        }
    }

    public OrderResponse getOrder(final Integer orderId) {
        Order order = orderRepository.findById(orderId).get();
        if(order == null){
            throw new OrderNotFoundException();
        }
        final OrderResponse orderUpdateResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(order);
        return orderUpdateResponse;
    }

    public List<OrderResponse> getOrdersForCoffeeShop(final Integer shopId) {
        final List<Order> ordersForCoffeeShop = orderRepository.findByCoffeeShopId(shopId);
        final List<OrderResponse> responseList = OrderRequestResponseTransformer.CovertEntityListToResponseList(ordersForCoffeeShop);
        return responseList;
    }

    private OrderMetaData calculateNextQueueNumberAndDeliveryTime(final List<Order> pendingOrders) {
        final OrderMetaData orderMetaData = new OrderMetaData();
        if(CollectionUtils.isEmpty(pendingOrders)) {
            orderMetaData.setNextQueueNumber("A1");
            orderMetaData.setExpectedDeliveryTime(LocalDateTime.now().plusMinutes(15));
            return orderMetaData;
        }
        final HashMap<Character, Integer> queueNumbersMap = new HashMap<>();
        final CoffeeShop coffeeShop = coffeeShopRepository.findById(pendingOrders.get(0).getCoffeeShopId()).get();
        final int queueCountForCoffeeShop = coffeeShop.getQueueCount();

        for(final Order pendingOrder: pendingOrders) {
            final Character currentQueueLetter = pendingOrder.getQueueNumber().charAt(0);
            final Integer mapValue = queueNumbersMap.get(currentQueueLetter);
            if (mapValue == null || mapValue < Integer.parseInt(pendingOrder.getQueueNumber().substring(1))) {
                queueNumbersMap.put(currentQueueLetter, Integer.parseInt(pendingOrder.getQueueNumber().substring(1)));
            }
        }

        // If any queue is empty
        if(queueNumbersMap.size() < queueCountForCoffeeShop){
            final List<Character> preDefinedQueueLetters = new ArrayList<>(Arrays.asList('A', 'B', 'C'));
            preDefinedQueueLetters.removeAll(queueNumbersMap.keySet());
            orderMetaData.setNextQueueNumber(String.valueOf(preDefinedQueueLetters.get(0)) + 1);
            orderMetaData.setExpectedDeliveryTime(LocalDateTime.now().plusMinutes(15));
            return orderMetaData;
        }

        final Character minValueKey = queueNumbersMap.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
        final Integer minQueueValue = queueNumbersMap.get(minValueKey);
        final Order currentOrderWithMinQueueNumber= pendingOrders.stream().filter(order -> order.getQueueNumber().equals(String.valueOf(minValueKey)+minQueueValue)).findFirst().get();
        final String nextQueueNumber = String.valueOf(minValueKey) + (minQueueValue + 1);
        final LocalDateTime expectedDeliveryTime = currentOrderWithMinQueueNumber.getExpectedDeliveryTime().plusMinutes(15);
        orderMetaData.setExpectedDeliveryTime(expectedDeliveryTime);
        orderMetaData.setNextQueueNumber(nextQueueNumber);
        return orderMetaData;
    }

}
