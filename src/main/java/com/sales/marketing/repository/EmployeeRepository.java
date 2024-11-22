package com.sales.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.sales.marketing.model.Employee;

@Transactional (readOnly  = true)
public interface EmployeeRepository<P> extends JpaRepository<Employee, Long> {

	List<Employee> findEmployeeByEmployeeId(Long employeeId);

	@Query(value ="select B.dealer_id,C.dealer_name from samdbschema.employee A,samdbschema.emp_business_coverage_area B, samdbschema.d_dealer_info C where A.user_role_c ='SR' and A.employee_id=B.employee_id AND B.dealer_id = C.dealer_id and b.employee_id=?1", nativeQuery = true)
	List<Object[]> findSalesRepDealers(Long employeeId);

	@Query(value="SELECT EMPLOYEE_ID,FIRST_NAME,MIDDLE_NAME,LAST_NAME FROM SAMDBSCHEMA.EMPLOYEE WHERE USER_ROLE_C = 'SR' AND INACTIVE_D IS NULL",nativeQuery=true)
	List<Object[]> findSalesRepEmpoyee();
}
