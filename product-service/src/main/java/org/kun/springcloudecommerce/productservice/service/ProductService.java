package org.kun.springcloudecommerce.productservice.service;

import org.kun.springcloudecommerce.productservice.model.ProductRequest;
import org.kun.springcloudecommerce.productservice.model.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    ResponseEntity<String> deleteAllProduct();

    List<ProductResponse> getAllProduct();

    void reduceQuantity(long productId, long quantity);

    void increaseQuantity(long productId, long quantity);
}
