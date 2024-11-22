package com.sales.marketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sales.marketing.feignclient.ManageCustomerClient;
import com.sales.marketing.feignclient.ManageSourceClient;

@Service
public class ExternalService {
	private final ManageCustomerClient manageCustomerClient;
	private final ManageSourceClient manageSourceClient;

    @Autowired
    public ExternalService(ManageCustomerClient manageCustomerClient, ManageSourceClient manageSourceClient) {
        this.manageCustomerClient = manageCustomerClient;
        this.manageSourceClient = manageSourceClient;
    }

    public String callServices() {
        String manageCustomerClientResponse = manageCustomerClient.getManageCustomer();
        String manageSourceClientResponse = manageSourceClient.getManageSource();

        return "Response from manage-customer: " + manageCustomerClientResponse + "\n" +
               "Response from manage-source: " + manageSourceClientResponse;
    }
}
