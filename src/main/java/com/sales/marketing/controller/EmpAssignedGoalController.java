package com.sales.marketing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sales.marketing.model.EmployeeAssignedGoal;
import com.sales.marketing.service.EmpAssignedFGoalService;
import com.sales.marketing.utils.JSONListConverter;
@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/employeeassignedgoals")
public class EmpAssignedGoalController {

	@Autowired
	EmpAssignedFGoalService empAssignedFGoalService;

	@GetMapping
	public String retrieveEmployeeAssignedGoals() throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(empAssignedFGoalService.getEmployeeAssignedGoals(),
				"/employeeassignedgoals/", "");
	}

	@GetMapping(value = "/{employeeId}")
	public String retrieveEmployeeAssignedGoals(@PathVariable Long employeeId) throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(empAssignedFGoalService.retrieveEmployeeAssignedGoals(employeeId),
				"/empbusinesscoveragearea/", employeeId.toString());
	}

	@PostMapping
	public HttpStatus addEmployeeAssginedGoals(@RequestBody List<EmployeeAssignedGoal> employeeAssignedGoals) {
		boolean result = empAssignedFGoalService.addEmployeeAssignedGoals(employeeAssignedGoals);
		if (result) {
			return HttpStatus.CREATED;
		} else {
			return HttpStatus.BAD_REQUEST;
		}
	}

	@PutMapping
	public HttpStatus updateEmployeeAssginedGoals(@RequestBody List<EmployeeAssignedGoal> employeeAssignedGoals) {
		boolean result = empAssignedFGoalService.updateEmployeeAssignedGoals(employeeAssignedGoals);
		if (result) {
			return HttpStatus.ACCEPTED;
		} else {
			return HttpStatus.BAD_REQUEST;
		}
	}

	@DeleteMapping(value = "/deletegoalsbyemployeeid/{employeeId}")
	public HttpStatus deleteEmployeeAssignedGoals(@PathVariable Long employeeId) {
		empAssignedFGoalService.deleteEmployeeAssignedGoals(employeeId);
		return HttpStatus.NO_CONTENT;
	}
}
