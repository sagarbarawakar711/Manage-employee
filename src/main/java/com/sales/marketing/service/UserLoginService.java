package com.sales.marketing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.UserLogin;
import com.sales.marketing.repository.UserLoginRepository;

@Service
public class UserLoginService {
	@Autowired
	UserLoginRepository<UserLogin> userLoginRepository;

	@Transactional
	public List<UserLogin> getAllUserLogins() {
		return (List<UserLogin>) userLoginRepository.findAll();
	}

	@Transactional
	public List<UserLogin> findByUserId(String userId) {
		return userLoginRepository.findByUserId(userId);
	}

	@Transactional
	public List<UserLogin> findByEmployeeId(Long employeeId) {
		return userLoginRepository.findByEmployeeId(employeeId);
	}
	@Transactional
	public boolean addUserLogin(UserLogin userlogin) {
		return userLoginRepository.save(userlogin) != null;
	}

	@Transactional
	public boolean updateUserLogin(UserLogin userlogin) {
		return userLoginRepository.save(userlogin) != null;
	}

	@Transactional
	public void deleteUserLogin(Long employeeId) {
		userLoginRepository.deleteByEmployeeId(employeeId);
	}
}
