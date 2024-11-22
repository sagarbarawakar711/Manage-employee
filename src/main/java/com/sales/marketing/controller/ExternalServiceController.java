package com.sales.marketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sales.marketing.service.ExternalService;

@RestController
@RequestMapping("/manage-employee")
public class ExternalServiceController {
	private final ExternalService externalService;

    @Autowired
    public ExternalServiceController(ExternalService externalService) {
        this.externalService = externalService;
    }

    @GetMapping
    public String callServices() {
        return externalService.callServices();
    }
}
