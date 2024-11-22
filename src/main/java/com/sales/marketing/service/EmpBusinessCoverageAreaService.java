package com.sales.marketing.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.EmpBusinessCoverageArea;
import com.sales.marketing.model.EmpBusinessCoverageAreaPK;
import com.sales.marketing.repository.EmpBusinessCoverageAreaRepository;

@Service
public class EmpBusinessCoverageAreaService {

	@Autowired
	EmpBusinessCoverageAreaRepository<EmpBusinessCoverageArea> empBusinessCoverageAreaRepository;
	
	@Transactional
	public List<EmpBusinessCoverageArea> getEmpBusinessCoverageAreas() {
		return (List<EmpBusinessCoverageArea>) empBusinessCoverageAreaRepository.findAll();
	}

	@Transactional
	public List<EmpBusinessCoverageArea> findByEmployeeId(Long employeeId){
		return empBusinessCoverageAreaRepository.findByIdEmployeeIdOrderByIdStateFipsCDesc(employeeId);
	}

	@Transactional
	public Optional<EmpBusinessCoverageArea> findBusinessCoverageAreas(EmpBusinessCoverageAreaPK id){
		return empBusinessCoverageAreaRepository.findById(id);
	}
	
	@Transactional
	public boolean addEmpBusinessCoverageArea(List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		return empBusinessCoverageAreaRepository.saveAll(empBusinessCoverageArea) != null;
	}

	@Transactional
	public boolean updateEmpBusinessCoverageArea(List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		return empBusinessCoverageAreaRepository.saveAll(empBusinessCoverageArea) != null;
	}
	
	
	@Transactional
	public void deleteEmpBusinessCoverageArea(List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		empBusinessCoverageAreaRepository.deleteInBatch(empBusinessCoverageArea);
	}
	
	@Transactional
	public void deleteByEmployeeId(Long employeeId) {
		empBusinessCoverageAreaRepository.deleteByIdEmployeeId(employeeId);
	}
	
	@Transactional
	public List<EmpBusinessCoverageArea> findByDealerId(Long dealerId){
		return empBusinessCoverageAreaRepository.findByIdDealerId(dealerId);
	}
	
	@Transactional
	public void deleteByDealerId(Long dealerId){
		empBusinessCoverageAreaRepository.deleteByIdDealerId(dealerId);
	}
}
