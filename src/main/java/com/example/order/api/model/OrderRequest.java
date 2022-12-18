package com.example.order.api.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Holds meta data for Order create and update request
 *
 * @author Tharaka Weheragoda
 */
@Data
public class OrderRequest {
    @NotNull(message = "customerId is mandatory")
    private Integer customerId;
    @NotNull(message = "coffeeShopId is mandatory")
    private Integer coffeeShopId;
    @NotNull(message = "items is mandatory")
    private List<Integer> items;
}
