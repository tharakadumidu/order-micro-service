package com.example.order.transformer;

import com.example.order.api.model.OrderRequest;
import com.example.order.api.model.OrderResponse;
import com.example.order.repository.entity.CoffeeShopItemPricing;
import com.example.order.repository.entity.Order;
import com.example.order.util.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Utility class to transform request to database entity and database entity to response
 *
 * @author Tharaka Weheragoda
 */
public class OrderRequestResponseTransformer {

    public static Order convertRequestToEntity(final OrderRequest orderRequest, final List<CoffeeShopItemPricing> coffeeShopItemPricingList) {
        final Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setCoffeeShopId(coffeeShopItemPricingList.get(0).getShopId());
        order.setCreatedTime(LocalDateTime.now());
        order.setPrice(calculateOrderPrice(coffeeShopItemPricingList));
        order.setStatus(OrderStatus.CREATED);
        return order;
    }

    private static double calculateOrderPrice(final List<CoffeeShopItemPricing> coffeeShopItemPricingList) {
        double totalPrice = 0;
        for(final CoffeeShopItemPricing item: coffeeShopItemPricingList){
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    public static OrderResponse convertOrderEntityToResponse(final Order order, final List<Integer> items) {
        final OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setPrice(order.getPrice());
        orderResponse.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        orderResponse.setQueueNumber(order.getQueueNumber());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setItems(items);
        return orderResponse;
    }

    public static Order updateOrderToEntity(final OrderRequest orderRequest, final Order previousOrder, final List<CoffeeShopItemPricing> coffeeShopItemPricingList) {
        previousOrder.setPrice(calculateOrderPrice(coffeeShopItemPricingList));
        return previousOrder;
    }
}
