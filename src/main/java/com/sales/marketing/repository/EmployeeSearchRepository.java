package com.sales.marketing.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sales.marketing.model.VDealerGrade;
import com.sales.marketing.model.VEmployeeSearch;


public interface EmployeeSearchRepository<P> extends JpaRepository<VEmployeeSearch, Long> {

	@Query("SELECT e FROM VEmployeeSearch e WHERE (:firstName is null or upper(e.firstName) like :firstName%) and (:lastName is null or upper(e.lastName) like :lastName%) and (:userRoleC is null or e.userRoleC =:userRoleC) and (((:status is null or :status = 'A') and e.inactiveD is null) or ((:status is null or :status = 'IA') AND e.inactiveD is not null))")
	List<VEmployeeSearch> searchEmployees(@Param("firstName") String firstName, @Param("lastName") String lastName,@Param("userRoleC") String userRoleC, @Param("status") String employeeStatus);
	
	@Query("SELECT dg FROM VDealerGrade dg WHERE dg.employeeId =:employeeId AND dg.dealerGrade IN (:grades) ")
	List<VDealerGrade> searchEmpDealerGradeDetails(@Param("employeeId") int employeeId, @Param("grades") List<String> grades);

	@Query(value ="select cast(employee_id as integer) from  samdbschema.emp_business_coverage_area where dealer_id = :dealerId and inactive_d is null", nativeQuery = true)
	List<Integer> getEmployeesForDealerId(@Param("dealerId") Integer dealerId);
	
	@Query("SELECT e FROM VEmployeeSearch e WHERE  e.employeeId in (:employeeIdList) and e.inactiveD is null")
	List<VEmployeeSearch> searchEmployeesList(@Param("employeeIdList") List<Integer> employeeIdList);
	

	
}
