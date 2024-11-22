package com.sales.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.UserLogin;
@Transactional (readOnly  = true)
public interface UserLoginRepository<P> extends JpaRepository<UserLogin, Long> {
	
	List<UserLogin> findByUserId(String userId);
	
	List<UserLogin> findByEmployeeId(Long employeeId);
	
	long deleteByEmployeeId(Long employeeId);
	
}
