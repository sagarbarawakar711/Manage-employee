package com.sales.marketing.vo;

public class SearchVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	String emailId;
	String passWord;
	String newPassWord;
	String firstName;
	String lastName;
	String roleType;
	String statusCode;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getNewPassWord() {
		return newPassWord;
	}
	public void setNewPassWord(String newPassWord) {
		this.newPassWord = newPassWord;
	}
	
	@Override
	public String toString() {
		return "SearchVO [emailId=" + emailId + ", firstName=" + firstName + ", lastName=" + lastName + ", roleType=" + roleType + ", statusCode=" + statusCode + "]";
	}	
	
}
