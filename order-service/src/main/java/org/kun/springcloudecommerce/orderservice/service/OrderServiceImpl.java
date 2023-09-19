package org.kun.springcloudecommerce.orderservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.orderservice.entity.Order;
import org.kun.springcloudecommerce.orderservice.exception.CustomException;
import org.kun.springcloudecommerce.orderservice.external.client.PaymentService;
import org.kun.springcloudecommerce.orderservice.external.client.ProductService;
import org.kun.springcloudecommerce.orderservice.external.request.PaymentRequest;
import org.kun.springcloudecommerce.orderservice.external.response.PaymentResponse;
import org.kun.springcloudecommerce.orderservice.external.response.ProductResponse;
import org.kun.springcloudecommerce.orderservice.model.OrderRequest;
import org.kun.springcloudecommerce.orderservice.model.OrderResponse;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    @Autowired
    private RestTemplate restTemplate;

    @Value("%{microservices.product}")
    private String productServiceUrl;

    @Value("%{microservices.payment}")
    private String paymentServiceUrl;


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
            paymentService.doPayment(paymentRequest);
            orderStatus = "PLACED";
            log.info("Payment done successfully.");

        } catch (Exception e) {
            log.info("Error occurred in payment");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        


        log.info("Order placed successfully with order id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order id: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException("The order with given id not found", "ORDER_NOT_FOUND", HttpStatus.NOT_FOUND)
        );

        log.info("Fetch the product information by product id: {}", order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject(
                productServiceUrl + order.getProductId(),
                ProductResponse.class
        );

        log.info("Fetch the payment details by order id: {}", order.getId());
        PaymentResponse paymentResponse = restTemplate.getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails.builder()
                        .name(productResponse.getName())
                        .productId(productResponse.getProductId())
                        .price(productResponse.getPrice())
                        .build();

        OrderResponse.PaymentDetails paymentDetails =
                OrderResponse.PaymentDetails.builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentStatus(paymentResponse.getStatus())
                        .paymentDate(paymentResponse.getPaymentDate())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .build();

        return OrderResponse.builder()
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderId(orderId)
                .orderStatus(order.getOrderStatus())
                .quantity(order.getQuantity())
                .paymentDetails(paymentDetails)
                .productDetails(productDetails)
                .build();

    }
}
