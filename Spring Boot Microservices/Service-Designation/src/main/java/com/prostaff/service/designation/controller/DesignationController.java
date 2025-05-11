package com.prostaff.service.designation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.designation.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.DesignationBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.NewDesignation;
import com.prostaff.service.designation.service.DesignationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/designation")
@Tag(name = "DesignationController")
public class DesignationController {

	@Autowired
	private DesignationService designationService;
	
	@PostMapping("/get-all-designation-names")
	public ResponseEntity<List<String>> getAllDesignationNames(@RequestBody AdminEmailWrapper adminEmailWrapper) {
		return new ResponseEntity<List<String>>(designationService.getAllDesignationNames(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-employee-designation")
	public ResponseEntity<DesignationBasicInformation> getEmployeeDesignation(@RequestBody EmployeeEmailWrapper emopEmailWrapper){
		return new ResponseEntity<DesignationBasicInformation>(designationService.getEmployeeDesignation(emopEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-employees/{id}")
	public ResponseEntity<List<EmployeeBasicInformation>> getAllEmployees(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<EmployeeBasicInformation>>(designationService.getAllEmployees(id, adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-designations")
	public ResponseEntity<List<DesignationBasicInformation>> getAllDesignation(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<DesignationBasicInformation>>(designationService.getAllDesignations(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/update-employees/{id}")
	public ResponseEntity<Boolean> updateEmployees(@PathVariable Long id,@RequestBody List<String> employeeList){
		return new ResponseEntity<Boolean>(designationService.updateEmployees(id, employeeList),HttpStatus.OK);
	}
	
	@PutMapping("/update-current-designation/{id}")
	public ResponseEntity<Boolean> updateCurrentDesignation(@PathVariable Long id, @RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<Boolean>(designationService.updateCurrentDesignation(id, employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/add-designation")
	public ResponseEntity<Boolean> addDesignation(@RequestBody NewDesignation newDesignation){
		return new ResponseEntity<Boolean>(designationService.addDesignation(newDesignation),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-designation/{id}")
	public ResponseEntity<Boolean> deleteDesignation(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(designationService.deleteDesignation(id, adminEmailWrapper),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-employee")
	public ResponseEntity<Boolean> deleteEmployee(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(designationService.deleteEmployee(employeeEmailWrapper), HttpStatus.OK);
	}
	
	@PostMapping("/add-employee-to-designation/{id}")
	public ResponseEntity<Boolean> addEmployeeToDesignation(@PathVariable Long id, @RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(designationService.addEmployeeToDesignation(id, employeeEmailWrapper), HttpStatus.OK);
	}
}
