package org.kun.springcloudecommerce.productservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ProductServiceCustomException extends RuntimeException{
    private HttpStatus errorCode;

    public ProductServiceCustomException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
