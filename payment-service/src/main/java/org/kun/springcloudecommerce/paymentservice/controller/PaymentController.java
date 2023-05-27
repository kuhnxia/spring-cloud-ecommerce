package org.kun.springcloudecommerce.paymentservice.controller;

import org.kun.springcloudecommerce.paymentservice.model.PaymentRequest;
import org.kun.springcloudecommerce.paymentservice.model.PaymentResponse;
import org.kun.springcloudecommerce.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/doPayment")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        long id = paymentService.doPayment(paymentRequest);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<PaymentResponse> getProductByOrderId(@PathVariable("id") long orderId){
        PaymentResponse paymentResponse = paymentService.getProductByOrderId(orderId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.FOUND);
    }
}
