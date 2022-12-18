package com.example.order.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Holds meta data for Order item entity
 *
 * @author Tharaka Weheragoda
 */
@Entity
@Data
@Table(name= "order_item")
@IdClass(OrderItemId.class)
public class OrderItem {
    @Id
    @Column(name = "order_id")
    private int orderId;
    @Id
    @Column(name = "coffee_shop_menu_pricing_id")
    private int coffeeShopItemPricingId;

}
