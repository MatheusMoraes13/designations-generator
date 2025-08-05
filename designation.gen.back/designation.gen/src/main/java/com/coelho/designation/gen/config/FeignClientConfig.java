package com.coelho.designation.gen.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Value("${netbox.token}")
    private String apiToken;

    @Bean
    public RequestInterceptor requestInterceptor () {
            return requestTemplate -> {
                requestTemplate.header("Authorization","Token " + apiToken);
        };
    }
}
