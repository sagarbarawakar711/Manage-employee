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
import com.sales.marketing.model.EmpBusinessCoverageArea;
import com.sales.marketing.service.EmpBusinessCoverageAreaService;
import com.sales.marketing.utils.JSONListConverter;
@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/empbusinesscoveragearea")
public class EmpBusinessCoverageAreaController {

	@Autowired
	EmpBusinessCoverageAreaService service;

	@GetMapping
	public String getEmpBusinessCoverageAreas() throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(service.getEmpBusinessCoverageAreas(), "/empbusinesscoveragearea/",
				"");
	}

	@GetMapping(value = "/{employeeId}")
	public String getEmpBusinessCOverageAreaByEmpId(@PathVariable Long employeeId) throws JsonProcessingException {
		return new JSONListConverter().arrayToJson(service.findByEmployeeId(employeeId), "/empbusinesscoveragearea/",
				employeeId.toString());
	}

	@PostMapping
	public HttpStatus insertEmpBusinessCoverageArea(
			@RequestBody List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		return service.addEmpBusinessCoverageArea(empBusinessCoverageArea) ? HttpStatus.CREATED
				: HttpStatus.BAD_REQUEST;
	}

	@PutMapping
	public HttpStatus updateEmpBusinessCoerageArea(@RequestBody List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		return service.updateEmpBusinessCoverageArea(empBusinessCoverageArea) ? HttpStatus.ACCEPTED
				: HttpStatus.BAD_REQUEST;
	}

	@DeleteMapping
	public HttpStatus deleteEmpBusinessCoverageAreas(
			@RequestBody List<EmpBusinessCoverageArea> empBusinessCoverageArea) {
		service.deleteEmpBusinessCoverageArea(empBusinessCoverageArea);
		return HttpStatus.NO_CONTENT;
	}
	
	@GetMapping(value = "/dealer/{dealerId}")
	public List<EmpBusinessCoverageArea> getEmpBusinessCOverageAreaByDealerId(@PathVariable Long dealerId)  {
		return service.findByDealerId(dealerId);
	}

}
