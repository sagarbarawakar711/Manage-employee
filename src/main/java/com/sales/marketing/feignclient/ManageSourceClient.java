package com.sales.marketing.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.sales.marketing.config.FeignConfig;

@FeignClient(name = "manage-source", configuration = FeignConfig.class)
public interface ManageSourceClient {
	@GetMapping("/hello-manage-source")
    String getManageSource();
}
