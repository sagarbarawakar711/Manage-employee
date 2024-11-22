package com.sales.marketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {
	
	@Bean
    public RequestInterceptor customFeignClientInterceptor() {
        return new FeignClientInterceptor();
    }
}