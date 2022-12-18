package com.example.order.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "coffee_shop_menu_pricing")
public class CoffeeShopItemPricing {
    @Id
    private int id;
    @Column(name = "menu_id")
    private int menuId;
    @Column(name = "shop_id")
    private int shopId;
    @Column(name = "price")
    private double price;
}
