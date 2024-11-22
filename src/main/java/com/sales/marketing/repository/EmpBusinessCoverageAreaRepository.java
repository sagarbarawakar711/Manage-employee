package com.sales.marketing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.EmpBusinessCoverageArea;
import com.sales.marketing.model.EmpBusinessCoverageAreaPK;

@Transactional (readOnly  = true)
public interface EmpBusinessCoverageAreaRepository<P> extends JpaRepository<EmpBusinessCoverageArea, EmpBusinessCoverageAreaPK> {

	Optional<EmpBusinessCoverageArea> findById(EmpBusinessCoverageAreaPK id);
	
	List<EmpBusinessCoverageArea> findByIdEmployeeIdOrderByIdStateFipsCDesc(Long employeeId);
	
	void deleteByIdEmployeeId(Long employeeId);
	
	List<EmpBusinessCoverageArea> findByIdDealerId(Long dealerId);
	
	void deleteByIdDealerId(Long dealerId);
}
