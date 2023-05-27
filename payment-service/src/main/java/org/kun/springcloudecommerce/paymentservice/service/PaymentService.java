package org.kun.springcloudecommerce.paymentservice.service;

import org.kun.springcloudecommerce.paymentservice.model.PaymentRequest;
import org.kun.springcloudecommerce.paymentservice.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getProductByOrderId(long orderId);
}
