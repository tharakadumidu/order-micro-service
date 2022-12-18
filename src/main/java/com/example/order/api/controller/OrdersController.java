package com.example.order.api.controller;

import com.example.order.api.model.OrderRequest;
import com.example.order.api.model.OrderResponse;
import com.example.order.service.OrderService;
import com.example.order.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/orders")
    public ResponseEntity createOrder(@RequestBody OrderRequest orderRequest) {
        final OrderResponse response = orderService.createOrder(orderRequest);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/orders/{id}")
    public ResponseEntity updateOrder(@PathVariable Integer id, @RequestBody OrderRequest orderRequest) {
        final OrderResponse response = orderService.updateOrder(orderRequest, id);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/{id}")
    public ResponseEntity getOrder(@PathVariable Integer id) {
        final OrderResponse response = orderService.getOrder(id);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/coffeeShops/{id}")
    public ResponseEntity getAllOrdersForCoffeeShop(@PathVariable Integer shopId) {
        final List<OrderResponse> responseList = orderService.getOrdersForCoffeeShop(shopId);
        return new ResponseEntity(responseList, HttpStatus.OK);
    }

    @PutMapping(value = "/orders/{id}/status/{status}")
    public ResponseEntity updateStatus(@PathVariable Integer id, @PathVariable OrderStatus orderStatus) {
        orderService.updateStatus(orderStatus, id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
