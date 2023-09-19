package org.kun.springcloudecommerce.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kun.springcloudecommerce.orderservice.entity.Order;
import org.kun.springcloudecommerce.orderservice.exception.CustomException;
import org.kun.springcloudecommerce.orderservice.external.client.PaymentService;
import org.kun.springcloudecommerce.orderservice.external.client.ProductService;
import org.kun.springcloudecommerce.orderservice.external.request.PaymentRequest;
import org.kun.springcloudecommerce.orderservice.external.response.PaymentResponse;
import org.kun.springcloudecommerce.orderservice.external.response.ProductResponse;
import org.kun.springcloudecommerce.orderservice.model.OrderRequest;
import org.kun.springcloudecommerce.orderservice.model.OrderResponse;
import org.kun.springcloudecommerce.orderservice.model.PaymentMode;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @Value("${microservice.product}")
    private String productServiceUrl;

    @Value("${microservice.payment}")
    private String paymentServiceUrl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils
                .setField(orderService ,"productServiceUrl", productServiceUrl );
        ReflectionTestUtils
                .setField(orderService ,"paymentServiceUrl", paymentServiceUrl );
    }

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                productServiceUrl + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class
        )).thenReturn(getMockPaymentResponse());

        OrderResponse orderResponse = orderService.getOrderDetails(1);


        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                productServiceUrl+ order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class
        );

        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());

    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_Then_Not_Found() {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                assertThrows(CustomException.class,
                        ()-> orderService.getOrderDetails(1));

        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        OrderRequest orderRequest = getMockOrderRequest();
        Order order = getMockOrder();

        when(productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1l,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_When_Place_Order_Payment_Failed_then_Order_Placed() {
        OrderRequest orderRequest = getMockOrderRequest();
        Order order = getMockOrder();

        when(productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .amount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .name("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return  Order.builder()
                .orderDate(Instant.now())
                .orderStatus("PLACED")
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }

}