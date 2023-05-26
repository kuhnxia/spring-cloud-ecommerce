package org.kun.springcloudecommerce.orderservice.config;

import feign.codec.ErrorDecoder;
import org.kun.springcloudecommerce.orderservice.external.decoder.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
