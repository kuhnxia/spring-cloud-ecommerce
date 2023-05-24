package org.kun.springcloudecommerce.productservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponse {
    private long productId;
    private String name;
    private long price;
    private long quantity;
}
