package com.sales.marketing.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@RequestMapping("/hello-manage-employees")
    public String index() {
        return "Greetings from Spring Boot! for Manage Employees!";
    }
}
