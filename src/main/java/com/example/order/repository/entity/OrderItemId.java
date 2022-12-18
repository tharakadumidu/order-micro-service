package com.example.order.repository.entity;

import java.io.Serializable;

/**
 * Composite ID class for order item entity
 *
 * @author Tharaka Weheragoda
 */
public class OrderItemId implements Serializable {
    private int orderId;
    private int coffeeShopItemPricingId;

    public OrderItemId(){}

    public OrderItemId(final int orderId, final int coffeeShopItemPricingId){
        this.orderId = orderId;
        this.coffeeShopItemPricingId = coffeeShopItemPricingId;
    }
}
