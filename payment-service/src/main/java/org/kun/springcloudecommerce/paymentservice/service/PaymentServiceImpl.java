package org.kun.springcloudecommerce.paymentservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.paymentservice.entity.TransactionDetails;
import org.kun.springcloudecommerce.paymentservice.model.PaymentMode;
import org.kun.springcloudecommerce.paymentservice.model.PaymentRequest;
import org.kun.springcloudecommerce.paymentservice.model.PaymentResponse;
import org.kun.springcloudecommerce.paymentservice.repository.TransactionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;
    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("recording payment details: {}", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .amount(paymentRequest.getAmount())
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .paymentStatus("SUCCESS")
                .build();

        transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction completed with id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getProductByOrderId(long orderId) {
        log.info("Find payment details by order id: {}", orderId);

        TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);

        return PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .orderId(orderId)
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .build();
    }
}
