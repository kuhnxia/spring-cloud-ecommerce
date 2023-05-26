package org.kun.springcloudecommerce.paymentservice.service;

import org.kun.springcloudecommerce.paymentservice.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
