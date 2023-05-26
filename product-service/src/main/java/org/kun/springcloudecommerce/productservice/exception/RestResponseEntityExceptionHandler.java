package org.kun.springcloudecommerce.productservice.exception;

import org.kun.springcloudecommerce.productservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductServiceCustomException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceCustomException(ProductServiceCustomException exception){
        return new ResponseEntity<>(
                new ErrorResponse(exception.getErrorCode(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
