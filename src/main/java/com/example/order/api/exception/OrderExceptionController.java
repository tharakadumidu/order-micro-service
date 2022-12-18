package com.example.order.api.exception;

import com.example.order.exception.MenuItemInvalidException;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.OrderUpdateFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.UnexpectedTypeException;

/**
 * Exception handler
 *
 * @author Tharaka Weheragoda
 */
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

    @ExceptionHandler(value = MenuItemInvalidException.class)
    public ResponseEntity<Object> exception(MenuItemInvalidException exception) {
        return new ResponseEntity<>("Order contains wrong menu items", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnexpectedTypeException.class)
    public ResponseEntity<Object> exception(UnexpectedTypeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> exception(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

}
