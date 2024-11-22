package com.sales.marketing.controller;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.sales.marketing.controller.model.EmployeeDealerGradeDetails;
import com.sales.marketing.exception.ResourceNotFoundException;
import com.sales.marketing.model.DDealerInfo;
import com.sales.marketing.model.Employee;
import com.sales.marketing.model.VEmployeeSearch;
import com.sales.marketing.service.EmployeeService;
import com.sales.marketing.utils.JSONListConverter;
import com.sales.marketing.vo.SearchVO;

@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/employee")
public class EmployeeController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EmployeeService employeeService;

	@GetMapping
	public String getAllEmployee() throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(employeeService.getAllEmployees(), "/employee/", "");
	}

	@GetMapping(value = "/salesrep")
	public String getSalesRepEmployee() throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(employeeService.getSalesRepEmployees(), "/employee/salesrep", "");
	}

	@PostMapping
	public ResponseEntity<?> insertEmployeeInfo(@RequestBody Employee employee) throws Exception {
		try {
			Employee savedEmployee = employeeService.addEmployee(employee);
			logger.info("Employee added!!");
			return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
		} catch (DataIntegrityViolationException e) {
	        employee.setApiStatusMessage("Constraint Violation");
			String errorMessage = "Constraint Violation: " + e.getMessage();
			logger.error("Error: " + errorMessage);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		} catch (Exception e) {
			employee.setApiStatusMessage(e.getMessage());
			String errorMessage = "An error occurred: " + e.getMessage();
			logger.error("Error: " + errorMessage);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateEmployeeInfo(@RequestBody Employee employee) throws Exception {
		try {
			Employee savedEmployee = employeeService.updateEmployee(employee);
			if(savedEmployee != null && !savedEmployee.getApiStatusMessage().equalsIgnoreCase("User roles empty")) {				
				return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
			} else if (savedEmployee.getApiStatusMessage().equalsIgnoreCase("User roles empty")) {
				logger.error("Error: User roles empty");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User roles array required at least one role.");
			} else {
				logger.error("Error: Employee not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
			}
		} catch (DataIntegrityViolationException e) {
	        employee.setApiStatusMessage("Constraint Violation");
			String errorMessage = "Constraint Violation: " + e.getMessage();
			logger.error("Error: " + errorMessage);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		} catch (Exception e) {
			employee.setApiStatusMessage(e.getMessage());
			String errorMessage = "An error occurred: " + e.getMessage();
			logger.error("Error: " + errorMessage);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@GetMapping(value = "/{employeeId}")
	public String getEmployeeInfo(@PathVariable Long employeeId)
			throws JsonProcessingException, ResourceNotFoundException, ServiceException {
		try {
			List<Employee> list = employeeService.findEmployeeId(employeeId);
			return new JSONListConverter().arrayToJson(list, "/employee/", employeeId.toString());
		} catch (ServiceException e) {
			throw new ServiceException("Unkown exception");
		}
	}

	@DeleteMapping(value = "/deleteemployee/{employeeId}")
	public HttpStatus deleteEmployeeInfo(@PathVariable Long employeeId) {
		employeeService.deleteEmployee(employeeId);
		return HttpStatus.NO_CONTENT;
	}

	@PostMapping("/search")
	public List<VEmployeeSearch> searchEmployees(@RequestBody SearchVO search) throws JsonProcessingException {
		return employeeService.searchEmployees(search);
	}

	@GetMapping(value = "/salesrepdealers/{employeeId}")
	public String getSalesRepDealers(@PathVariable Long employeeId)
			throws JsonProcessingException, ResourceNotFoundException, ServiceException {
		try {
			List<DDealerInfo> list = employeeService.retrieveSalesRepDealers(employeeId);
			return new JSONListConverter().arrayToJson(list, "/salesrepdealers/", employeeId.toString());
		} catch (ServiceException e) {
			throw new ServiceException("Unkown exception");
		}
	}

	@GetMapping(value = "/dealergrade/{employeeId}")
	// consumes= {MediaType.APPLICATION_JSON_VALUE},produces=
	// {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<EmployeeDealerGradeDetails>> getDealerColorDetails(@PathVariable int employeeId,
			@RequestParam List<String> colorCodes) {

		// Check if the values are empty?
		if (colorCodes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(employeeService.getEmployeeDealerGradeDetails(employeeId, colorCodes));
	}

	@PostMapping("/getSalesAgent/{dealerId}")
	public List<VEmployeeSearch> searchEmployees(@PathVariable Integer dealerId) throws JsonProcessingException {
		return employeeService.getEmployeesForDealerId(dealerId);
	}

}