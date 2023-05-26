package org.kun.springcloudecommerce.orderservice.exception;
import org.kun.springcloudecommerce.orderservice.external.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrderServiceCustomException.class)
    public ResponseEntity<ErrorResponse> handleOrderServiceCustomException(OrderServiceCustomException exception){
        return new ResponseEntity<>(
                new ErrorResponse(exception.getErrorCode(), exception.getMessage()),
                HttpStatus.valueOf(exception.getStatus())
        );
    }

}
