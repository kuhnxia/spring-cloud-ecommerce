package org.kun.springcloudecommerce.productservice.service;

import org.kun.springcloudecommerce.productservice.model.ProductRequest;
import org.kun.springcloudecommerce.productservice.model.ProductResponse;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);
}
