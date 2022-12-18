package com.example.order.api.controller;

import com.example.order.api.model.OrderRequest;
import com.example.order.api.model.OrderResponse;
import com.example.order.service.OrderService;
import com.example.order.util.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Orders API request controller
 *
 * @author Tharaka Weheragoda
 */

@RestController
public class OrdersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/orders")
    public ResponseEntity createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        LOGGER.info("order Create request received");
        final OrderResponse response = orderService.createOrder(orderRequest);
        LOGGER.info("order Create request completed");
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/orders/{id}")
    public ResponseEntity updateOrder(@PathVariable Integer id, @Valid @RequestBody OrderRequest orderRequest) {
        LOGGER.info("order Update request received");
        final OrderResponse response = orderService.updateOrder(orderRequest, id);
        LOGGER.info("order Update request completed");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/{id}")
    public ResponseEntity getOrder(@PathVariable Integer id) {
        LOGGER.info("view order request received");
        final OrderResponse response = orderService.getOrder(id);
        LOGGER.info("view order request completed");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/coffeeShops/{id}")
    public ResponseEntity getAllOrdersForCoffeeShop(@PathVariable("id") Integer shopId) {
        LOGGER.info("view orders for coffee shop request received");
        final List<OrderResponse> responseList = orderService.getOrdersForCoffeeShop(shopId);
        LOGGER.info("view orders for coffee shop request completed");
        return new ResponseEntity(responseList, HttpStatus.OK);
    }

    @PutMapping(value = "/orders/{id}/status/{status}")
    public ResponseEntity updateStatus(@PathVariable("id") Integer id, @PathVariable("status") OrderStatus orderStatus) {
        LOGGER.info("Update status request received");
        orderService.updateStatus(orderStatus, id);
        LOGGER.info("Update status request completed");
        return new ResponseEntity(HttpStatus.OK);
    }
}
