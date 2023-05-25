package org.kun.springcloudecommerce.productservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ProductServiceCustomException extends RuntimeException{
    private String errorCode;

    public ProductServiceCustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
