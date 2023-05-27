package org.kun.springcloudecommerce.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private long paymentId;
    private String status;
    private PaymentMode paymentMode;
    private long amount;
    private long orderId;
    private Instant paymentDate;
}
