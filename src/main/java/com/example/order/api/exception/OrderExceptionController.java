package com.example.order.api.exception;

import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.OrderUpdateFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionController {
    @ExceptionHandler(value = OrderNotFoundException.class)
    public ResponseEntity<Object> exception(OrderNotFoundException exception) {
        return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OrderUpdateFailedException.class)
    public ResponseEntity<Object> exception(OrderUpdateFailedException exception) {
        return new ResponseEntity<>("Order update cannot be proceed", HttpStatus.FORBIDDEN);
    }
}
