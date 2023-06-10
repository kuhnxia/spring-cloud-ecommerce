package org.kun.springcloudecommerce.orderservice.exception;
import org.kun.springcloudecommerce.orderservice.external.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleOrderServiceCustomException(CustomException exception){
        return new ResponseEntity<>(
                new ErrorResponse(exception.getErrorCode(), exception.getMessage()),
                exception.getStatus()
        );
    }

}
