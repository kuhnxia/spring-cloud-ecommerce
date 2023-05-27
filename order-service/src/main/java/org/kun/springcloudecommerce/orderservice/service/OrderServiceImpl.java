package org.kun.springcloudecommerce.orderservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.orderservice.entity.Order;
import org.kun.springcloudecommerce.orderservice.exception.OrderServiceCustomException;
import org.kun.springcloudecommerce.orderservice.external.client.PaymentService;
import org.kun.springcloudecommerce.orderservice.external.client.ProductService;
import org.kun.springcloudecommerce.orderservice.external.request.PaymentRequest;
import org.kun.springcloudecommerce.orderservice.model.OrderRequest;
import org.kun.springcloudecommerce.orderservice.model.OrderResponse;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;


    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Place order request: {}", orderRequest);
        //Product Service -> Block Products(Reduce the Quantity)
        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());
        //Order Entity -> Save thr data with Status Order created
        log.info("Creating order with status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getAmount())
                .orderStatus("Created")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);

        log.info("Calling payment service to complete the payment.");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentMode(orderRequest.getPaymentMode())
                .orderId(order.getId())
                .amount(order.getAmount())
                .build();

        String orderStatus;
        try {
            log.info("Payment done successfully.");
            paymentService.doPayment(paymentRequest);
            orderStatus = "PLACED";

        } catch (Exception e) {
            log.info("Error occurred in payment");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);;
        


        log.info("Order placed successfully with order id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order id: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderServiceCustomException("The order with given id not found", "ORDER_NOT_FOUND", HttpStatus.NOT_FOUND)
        );

        return OrderResponse.builder()
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderId(orderId)
                .orderStatus(order.getOrderStatus())
                .build();

    }
}
