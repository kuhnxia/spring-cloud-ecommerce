package org.kun.springcloudecommerce.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.commons.util.StringUtils;
import org.kun.springcloudecommerce.orderservice.OrderServiceConfig;
import org.kun.springcloudecommerce.orderservice.entity.Order;
import org.kun.springcloudecommerce.orderservice.model.OrderRequest;
import org.kun.springcloudecommerce.orderservice.model.OrderResponse;
import org.kun.springcloudecommerce.orderservice.model.PaymentMode;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.kun.springcloudecommerce.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.StreamUtils.*;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = OrderServiceConfig.class)
public class OrderControllerTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockExtension =
            WireMockExtension.newInstance()
                    .options(WireMockConfiguration
                            .wireMockConfig()
                            .port(8080))
                    .build();

    private ObjectMapper objectMapper =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        wireMockExtension.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getPaymentDetails() throws IOException {
        wireMockExtension.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        OrderControllerTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("mock/GetPayment.json"),
                                        defaultCharset()
                                )
                        )));
    }

    private void doPayment() {
        wireMockExtension.stubFor(post(urlEqualTo("/payment/doPayment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        wireMockExtension.stubFor(
                WireMock.get("/product/1")
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(
                                                StreamUtils.copyToString(
                                                       OrderControllerTest.class
                                                               .getClassLoader()
                                                               .getResourceAsStream("mock/GetProduct.json"),
                                                       defaultCharset()
                                                ))));
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .amount(200)
                .build();
    }

    @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        OrderRequest orderRequest = getMockOrderRequest();

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/order/placeOrder").with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("Customer")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

        Optional<Order>  order = orderRepository.findById(Long.valueOf(orderId));
        assertTrue(order.isPresent());

        Order o = order.get();
        assertEquals(Long.parseLong(orderId), o.getId());
        assertEquals("PLACED", o.getOrderStatus());
        assertEquals(orderRequest.getAmount(), o.getAmount());
        assertEquals(orderRequest.getQuantity(), o.getQuantity());

    }

    @Test
    public void test_WhenPlaceOrderWithWrongAccess_Throw403() throws Exception {
        OrderRequest orderRequest = getMockOrderRequest();

        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/order/placeOrder").with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_WhenGetOrder_Success() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/order/1").with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        Order order = orderRepository.findById(1l).get();
        String expectResponse = getOrderResponse(order);

        assertEquals(expectResponse, actualResponse);
    }

    @Test
    public void test_WhenGetOrder_OrderNotFound() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/order/2").with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    private String getOrderResponse(Order order) throws IOException {
        OrderResponse.PaymentDetails paymentDetails
                = objectMapper.readValue(copyToString(OrderControllerTest.class
                        .getClassLoader()
                        .getResourceAsStream("mock/GetPayment.json"),
                defaultCharset()),
                OrderResponse.PaymentDetails.class);
        paymentDetails.setPaymentStatus("SUCCESS");

        OrderResponse.ProductDetails productDetails
                = objectMapper.readValue(copyToString(OrderControllerTest.class
                                .getClassLoader()
                                .getResourceAsStream("mock/GetProduct.json"),
                        defaultCharset()),
                OrderResponse.ProductDetails.class);

        OrderResponse orderResponse = OrderResponse.builder()
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderId(order.getId())
                .quantity(order.getQuantity())
                .build();
        return objectMapper.writeValueAsString(orderResponse);
    }


}