package org.kun.springcloudecommerce.productservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.productservice.entity.Product;
import org.kun.springcloudecommerce.productservice.model.ProductRequest;
import org.kun.springcloudecommerce.productservice.model.ProductResponse;
import org.kun.springcloudecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");

        Product product
                = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        productRepository.save(product);

        log.info("Product added");

        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("find product by id: {}", productId);

        Product product = productRepository
                .findById(productId)
                .orElseThrow(()->new RuntimeException("Product with given id not found"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return  productResponse;
    }
}
