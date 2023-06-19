package org.kun.springcloudecommerce.orderservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kun.springcloudecommerce.orderservice.external.client.PaymentService;
import org.kun.springcloudecommerce.orderservice.external.client.ProductService;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class OrderServiceImplTest {
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


    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {

    }

}