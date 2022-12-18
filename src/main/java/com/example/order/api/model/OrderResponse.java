package com.example.order.api.model;

import com.example.order.util.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private int orderId;
    private double price;
    private OrderStatus status;
    private LocalDateTime expectedDeliveryTime;
    private String queueNumber;
}
