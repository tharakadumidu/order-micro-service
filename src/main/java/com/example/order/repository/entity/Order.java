package com.example.order.repository.entity;

import com.example.order.util.OrderStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "coffee_shop_id")
    private int coffeeShopId;
    @Column(name = "price")
    private double price;
    @Column(name = "status")
    private OrderStatus status;
    @Column(name = "queue_number")
    private String queueNumber;
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Column(name = "expected_delivery_time")
    private LocalDateTime expectedDeliveryTime;
}
