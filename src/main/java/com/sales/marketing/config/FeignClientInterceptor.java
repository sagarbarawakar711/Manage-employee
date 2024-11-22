package com.sales.marketing.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
	@Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            String userId = request.getHeader("Userid");
            String token = request.getHeader("Authorization");

            if (userId != null) {
                requestTemplate.header("Userid", userId);
            }

            if (token != null) {
                requestTemplate.header("Authorization", token);
            }
        }
    }
}
