package com.sales.marketing.controller.model;

public class EmployeeDealerGradeDetails {

	private int empId;
	private int dealerId;
	private String dealerName;
	private String dealerColorCode;
	
	public EmployeeDealerGradeDetails(int empId, int dealerId, String dealerName, String dealerColorCode ) {
		this.dealerColorCode = dealerColorCode;
		this.dealerId = dealerId;
		this.dealerName = dealerName;
		this.empId = empId;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public int getDealerId() {
		return dealerId;
	}

	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerColorCode() {
		return dealerColorCode;
	}

	public void setDealerColorCode(String dealerColorCode) {
		this.dealerColorCode = dealerColorCode;
	}
	
	
}
