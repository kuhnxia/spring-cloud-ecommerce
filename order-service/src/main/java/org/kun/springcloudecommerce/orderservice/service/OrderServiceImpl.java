package org.kun.springcloudecommerce.orderservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.orderservice.entity.Order;
import org.kun.springcloudecommerce.orderservice.external.client.ProductService;
import org.kun.springcloudecommerce.orderservice.model.OrderRequest;
import org.kun.springcloudecommerce.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;


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
        log.info("Order placed successfully with order id: {}", order.getId());
        //Product Service -> Block Products(Reduce the Quantity)
        //Payment Service -> Payments -> Success -> Complete, else Cancelled.
        return order.getId();
    }
}
