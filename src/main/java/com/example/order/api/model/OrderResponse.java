package com.example.order.api.model;

import com.example.order.util.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Holds meta data for Order create and update response
 *
 * @author Tharaka Weheragoda
 */
@Data
public class OrderResponse {
    private int orderId;
    private List<Integer> items;
    private double price;
    private OrderStatus status;
    private LocalDateTime expectedDeliveryTime;
    private String queueNumber;
}
