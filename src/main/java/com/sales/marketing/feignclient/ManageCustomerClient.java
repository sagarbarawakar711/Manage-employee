package com.sales.marketing.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.sales.marketing.config.FeignConfig;

@FeignClient(name = "manage-customer", configuration = FeignConfig.class)
public interface ManageCustomerClient {
	@GetMapping("/hello-manage-customer")
    String getManageCustomer();
}
