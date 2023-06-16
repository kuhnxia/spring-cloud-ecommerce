package org.kun.springcloudecommerce.paymentservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests(
                        authorizeRequest ->
                                authorizeRequest
                                        .antMatchers("/payment/**")
                                        .hasAnyAuthority("SCOPE_internal")
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return httpSecurity.build();
    }
}
