package com.sales.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.EmployeeAssignedGoal;

@Transactional (readOnly  = true)
public interface EmpAssignedGoalRepository extends JpaRepository<EmployeeAssignedGoal, Long> {

	List<EmployeeAssignedGoal> findEmployeeAssginedGoalsByEmployeeIdOrderByStateFipsCDesc(Long employeeId);
	
	void deleteEmployeeAssignedGoalByEmployeeIdAndStateFipsC(Long employeeId, String stateFipsC);
	
	@Query(value="SELECT DISTINCT STATE_FIPS_C FROM SAMDBSCHEMA.EMPLOYEE_ASSIGNED_GOALS where employee_id = ?1 AND  state_fips_c is not null", nativeQuery = true)
	List<Object[]> findEmployeeAssginedGoalsByEmployeeId(Long employeeId);

	void deleteEmpAssignedGoalsByEmployeeId(Long employeeId);

}
