package org.kun.springcloudecommerce.orderservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class OrderServiceCustomException extends RuntimeException{
    private String errorCode;
    private HttpStatus status;

    public OrderServiceCustomException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
