package com.example.order.api.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private int customerId;
    private int coffeeShopId;
    private List<Integer> items;
}
