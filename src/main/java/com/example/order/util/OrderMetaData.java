package com.example.order.util;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderMetaData {
    private String nextQueueNumber;
    private LocalDateTime expectedDeliveryTime;
}
