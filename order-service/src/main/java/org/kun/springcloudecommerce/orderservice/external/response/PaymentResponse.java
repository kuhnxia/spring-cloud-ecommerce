package org.kun.springcloudecommerce.orderservice.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kun.springcloudecommerce.orderservice.model.PaymentMode;

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
