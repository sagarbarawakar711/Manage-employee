package com.sales.marketing.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.EmployeeAssignedGoal;
import com.sales.marketing.repository.EmpAssignedGoalRepository;

@Service
public class EmpAssignedFGoalService {

	@Autowired
	EmpAssignedGoalRepository empAssignedGoalRepository;

	@Transactional
	public List<EmployeeAssignedGoal> getEmployeeAssignedGoals() {
		return (List<EmployeeAssignedGoal>) empAssignedGoalRepository.findAll();
	}

	@Transactional
	public List<EmployeeAssignedGoal> retrieveEmployeeAssignedGoals(Long employeeId) {
		return (List<EmployeeAssignedGoal>) empAssignedGoalRepository.findEmployeeAssginedGoalsByEmployeeIdOrderByStateFipsCDesc(employeeId);
	}

	@Transactional
	public void deleteEmployeeAssignedGoals(Long employeeId) {
		empAssignedGoalRepository.deleteEmpAssignedGoalsByEmployeeId(employeeId);
	}
	
	@Transactional
	public boolean addEmployeeAssignedGoals(List<EmployeeAssignedGoal> employeeAssignedGoals) {
		return empAssignedGoalRepository.saveAll(employeeAssignedGoals) != null;
	}
	
	@Transactional
	public boolean updateEmployeeAssignedGoals(List<EmployeeAssignedGoal> employeeAssignedGoals) {
		return empAssignedGoalRepository.saveAll(employeeAssignedGoals) != null;
	}

	@Transactional
	public void deleteEmpAssignedGoals(List<EmployeeAssignedGoal> empAssignedGoals) {
		empAssignedGoalRepository.deleteInBatch(empAssignedGoals);
	}
	
	public void deleteEmployeeAssignedGoalByEmployeeIdAndStateFipsC(Long employeeId, String stateFipsC) {
		empAssignedGoalRepository.deleteEmployeeAssignedGoalByEmployeeIdAndStateFipsC(employeeId,stateFipsC);
	}

	@SuppressWarnings("rawtypes")
	public List<String> getEmpAgentGoalsStates(Long employeeId){
		List<String> list = new ArrayList<String>();
		List<Object[]> records =(List<Object[]> ) empAssignedGoalRepository.findEmployeeAssginedGoalsByEmployeeId(employeeId);
		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			list.add((String)objects[0]);
		}
		return list; 
	}
}
