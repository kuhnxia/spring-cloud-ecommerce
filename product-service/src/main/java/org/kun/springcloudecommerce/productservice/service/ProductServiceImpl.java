package org.kun.springcloudecommerce.productservice.service;

import lombok.extern.log4j.Log4j2;
import org.kun.springcloudecommerce.productservice.entity.Product;
import org.kun.springcloudecommerce.productservice.exception.ProductServiceCustomException;
import org.kun.springcloudecommerce.productservice.model.ProductRequest;
import org.kun.springcloudecommerce.productservice.model.ProductResponse;
import org.kun.springcloudecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(()->new ProductServiceCustomException("Product with given id not found",
                        "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return  productResponse;
    }

    @Override
    public ResponseEntity<String> deleteAllProduct() {
        productRepository.deleteAll();
        return new ResponseEntity<>("All products are deleted", HttpStatus.OK);
    }

    @Override
    public List<ProductResponse> getAllProduct() {

        return productRepository.findAll().stream().map(product -> {
            ProductResponse productResponse = new ProductResponse();
            BeanUtils.copyProperties(product, productResponse);
            return productResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for Id: {}", quantity, productId);

        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new ProductServiceCustomException("Product with given id not found", "PRODUCT_NOT_FOUND")
        );

        if (product.getQuantity() < quantity || product.getQuantity() == 0){
            throw new ProductServiceCustomException("Product doesn't have sufficient quantity",
                    "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully");

    }
}
