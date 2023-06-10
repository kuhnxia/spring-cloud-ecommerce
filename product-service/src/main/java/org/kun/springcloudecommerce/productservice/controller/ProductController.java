package org.kun.springcloudecommerce.productservice.controller;

import org.kun.springcloudecommerce.productservice.model.ProductRequest;
import org.kun.springcloudecommerce.productservice.model.ProductResponse;
import org.kun.springcloudecommerce.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @PostMapping
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest){
        Long productId = productService.addProduct(productRequest);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long productId){
        ProductResponse productResponse = productService.getProductById(productId);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProduct(){
        List<ProductResponse> products = productService.getAllProduct();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllProduct(){
        return productService.deleteAllProduct();
    }

    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    ){
        productService.reduceQuantity(productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/increaseQuantity/{id}")
    public ResponseEntity<Void> increaseQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    ){
        productService.increaseQuantity(productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
