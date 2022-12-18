package com.example.order.service;

import com.example.order.api.model.OrderRequest;
import com.example.order.api.model.OrderResponse;
import com.example.order.exception.MenuItemInvalidException;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.OrderUpdateFailedException;
import com.example.order.repository.CoffeeShopItemPricingRepository;
import com.example.order.repository.CoffeeShopRepository;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.entity.CoffeeShop;
import com.example.order.repository.entity.CoffeeShopItemPricing;
import com.example.order.repository.entity.Order;
import com.example.order.repository.entity.OrderItem;
import com.example.order.transformer.OrderRequestResponseTransformer;
import com.example.order.util.OrderMetaData;
import com.example.order.util.OrderStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

/**
 * service class to handle orders
 *
 * @author Tharaka Weheragoda
 */
@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private CoffeeShopItemPricingRepository coffeeShopItemPricingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CoffeeShopRepository coffeeShopRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private Queue queue;
    @Autowired
    private JmsTemplate jmsTemplate;

    /*
    create a new order
    */
    public OrderResponse createOrder(final OrderRequest orderRequest) {
        final List<CoffeeShopItemPricing> coffeeShopItemPricingList =  new ArrayList<>();
        CollectionUtils.addAll(coffeeShopItemPricingList, coffeeShopItemPricingRepository.findAllById(orderRequest.getItems()));
        if(CollectionUtils.isEmpty(coffeeShopItemPricingList)){
            LOGGER.error("Wrong menu items provided in request");
            throw new MenuItemInvalidException();
        }
        Order order = OrderRequestResponseTransformer.convertRequestToEntity(orderRequest, coffeeShopItemPricingList);
        final List<Order> pendingOrders = orderRepository.findByCoffeeShopId(order.getCoffeeShopId());
        final OrderMetaData orderMetaData = calculateNextQueueNumberAndDeliveryTime(pendingOrders);
        order.setQueueNumber(orderMetaData.getNextQueueNumber());
        order.setExpectedDeliveryTime(orderMetaData.getExpectedDeliveryTime());
        order = orderRepository.save(order);

        //Save item list in order in a separate table
        for(final Integer itemId: orderRequest.getItems()){
            final OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setCoffeeShopItemPricingId(itemId);
            orderItemRepository.save(orderItem);
        }
        final OrderResponse orderResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(order, orderRequest.getItems());
        return orderResponse;
    }

    /*
    update a new order
    */
    public OrderResponse updateOrder(final OrderRequest orderRequest, final Integer orderId) {
        Order previousOrder = orderRepository.findById(orderId).get();
        if(previousOrder == null){
            LOGGER.error("Order not found with ID %s", orderId);
            throw new OrderNotFoundException();
        }
        if(!previousOrder.getStatus().equals(OrderStatus.CREATED)){
            LOGGER.error("Order update can't be proceed for orders in %s status", previousOrder.getStatus());
            throw new OrderUpdateFailedException();
        }
        final List<CoffeeShopItemPricing> coffeeShopItemPricingList =  new ArrayList<>();
        CollectionUtils.addAll(coffeeShopItemPricingList, coffeeShopItemPricingRepository.findAllById(orderRequest.getItems()));
        if(CollectionUtils.isEmpty(coffeeShopItemPricingList)){
            LOGGER.error("Wrong menu items provided in request");
            throw new MenuItemInvalidException();
        }
        previousOrder = OrderRequestResponseTransformer.updateOrderToEntity(orderRequest, previousOrder, coffeeShopItemPricingList);
        previousOrder = orderRepository.save(previousOrder);
        final OrderResponse orderUpdateResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(previousOrder, orderRequest.getItems());
        return orderUpdateResponse;
    }

    /*
    update order status
    */
    public void updateStatus(final OrderStatus orderStatus, final Integer orderId) {
        final Order order = orderRepository.findById(orderId).get();
        if(order == null){
            LOGGER.error("Order not found with ID %s", orderId);
            throw new OrderNotFoundException();
        }
        if(orderStatus.equals(OrderStatus.CANCELED) && !order.getStatus().equals(OrderStatus.CREATED)){
            LOGGER.error("Order status cannot be updated to CANCELED for orders in %s status", order.getStatus());
            throw new OrderUpdateFailedException();
        }
        order.setStatus(orderStatus);
        orderRepository.save(order);
        if(orderStatus.equals(OrderStatus.SERVED)){
            // sending order status change event to queue. Then event consumers can consume event.
            jmsTemplate.convertAndSend(queue, order);
        }
    }

    /*
    view a order
    */
    public OrderResponse getOrder(final Integer orderId) {
        final Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            LOGGER.error("Order not found with ID %s", orderId);
            throw new OrderNotFoundException();
        }
        final List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        final List<Integer> itemList = orderItems.stream()
                .map(OrderItem::getCoffeeShopItemPricingId)
                .collect(Collectors.toList());
        final OrderResponse orderUpdateResponse = OrderRequestResponseTransformer.convertOrderEntityToResponse(order, itemList);
        return orderUpdateResponse;
    }

    /*
    view orders for a coffee shop
    */
    public List<OrderResponse> getOrdersForCoffeeShop(final Integer shopId) {
        final List<Order> ordersForCoffeeShop = orderRepository.findByCoffeeShopId(shopId);
        final List<OrderResponse> responseList = new ArrayList<>();
        for(final Order order: ordersForCoffeeShop) {
            final List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            final List<Integer> itemList = orderItems.stream()
                    .map(OrderItem::getCoffeeShopItemPricingId)
                    .collect(Collectors.toList());
            responseList.add(OrderRequestResponseTransformer.convertOrderEntityToResponse(order, itemList));
        }
        return responseList;
    }

    /*
    Calculates the next queue number and expected delivery time
    */
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

        // If a shop is having multiple queues and any queue is empty
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
