package com.sales.marketing.vo;

import java.io.Serializable;

import com.sales.marketing.model.Employee;

public class UserResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long employeeId;
	private String userId;
	private String passwordHash;
	private String passwordSalt;
	private Employee employee;
	private String apiStatusMessage;
	private String jwtToken;

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public UserResponse() {
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordSalt() {
		return this.passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getApiStatusMessage() {
		return apiStatusMessage;
	}

	public void setApiStatusMessage(String apiStatusMessage) {
		this.apiStatusMessage = apiStatusMessage;
	}

}
