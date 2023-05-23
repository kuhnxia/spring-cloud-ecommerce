package org.kun.springcloudecommerce.productservice.service;

import org.kun.springcloudecommerce.productservice.model.ProductRequest;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);
}
