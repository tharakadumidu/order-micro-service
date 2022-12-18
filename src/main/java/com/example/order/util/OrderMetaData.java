package com.example.order.util;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Intermediate class to hold calculated nextQueueNumber and expectedDeliveryTime
 *
 * @author Tharaka Weheragoda
 */
@Data
public class OrderMetaData {
    private String nextQueueNumber;
    private LocalDateTime expectedDeliveryTime;
}
