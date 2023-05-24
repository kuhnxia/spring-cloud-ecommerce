package org.kun.springcloudecommerce.orderservice.service;

import org.kun.springcloudecommerce.orderservice.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
