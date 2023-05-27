package org.kun.springcloudecommerce.orderservice.external.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private long productId;
    private String name;
    private long price;
    private long quantity;
}
