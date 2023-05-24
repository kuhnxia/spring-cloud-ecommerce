package org.kun.springcloudecommerce.productservice.exception;

import org.kun.springcloudecommerce.productservice.model.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductServiceCustomException.class)
    public ResponseEntity<ErrorMessage> handleProductServiceCustomException(ProductServiceCustomException exception){
        return new ResponseEntity<>(
                new ErrorMessage(exception.getErrorCode(), exception.getMessage()),
                exception.getErrorCode());
    }

}
