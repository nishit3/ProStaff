package com.prostaff.service.department.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.department.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.DepartmentBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.NewDepartment;
import com.prostaff.service.department.service.DepartmentService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/department")
@Tag(name = "DepartmentController")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;
	
	@PostMapping("/get-all-department-names")
	public ResponseEntity<List<String>> getAllDepartmentNames(@RequestBody AdminEmailWrapper adminEmailWrapper) {
		return new ResponseEntity<List<String>>(departmentService.getAllDepartmentNames(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-employee-department")
	public ResponseEntity<DepartmentBasicInformation> getEmployeeDepartment(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<DepartmentBasicInformation>(departmentService.getEmployeeDepartment(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/add-department")
	public ResponseEntity<Boolean> addDepartmanet(@RequestBody NewDepartment newDepartment){
		return new ResponseEntity<Boolean>(departmentService.addDepartmanet(newDepartment),HttpStatus.OK);
	}
	
	@GetMapping("/get-all-employees/{id}")
	public ResponseEntity<List<EmployeeBasicInformation>> getAllEmployees(@PathVariable Long id){
		return new ResponseEntity<List<EmployeeBasicInformation>>(departmentService.getAllEmployees(id),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-departments")
	public ResponseEntity<List<DepartmentBasicInformation>> getAllDepartments(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<DepartmentBasicInformation>>(departmentService.getAllDepartments(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/update-employees/{departmentId}")
	public ResponseEntity<Boolean> updateEmployees(@PathVariable Long departmentId, @RequestBody List<String> employeeEmails){
		return new ResponseEntity<Boolean>(departmentService.updateEmployeesOfDepartment(departmentId, employeeEmails),HttpStatus.OK);
	}
	
	@PutMapping("/update-current-department/{departmentId}")
	public ResponseEntity<Boolean> updateCurrentDepartment(@PathVariable Long departmentId, @RequestBody EmployeeEmailWrapper epEmailWrapper){
		return new ResponseEntity<Boolean>(departmentService.updateCurrentDepartmentOfEmployee(departmentId, epEmailWrapper),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-department/{departmentId}")
	public ResponseEntity<Boolean> deleteDepartment(@PathVariable Long departmentId, @RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(departmentService.deleteDepartment(departmentId, adminEmailWrapper),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-employee")
	public ResponseEntity<Boolean> deleteEmployee(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(departmentService.deleteEmployee(employeeEmailWrapper), HttpStatus.OK);
	}
	
	@PostMapping("/add-employee-to-department/{id}")
	public ResponseEntity<Boolean> addEmployeeToDepartment(@PathVariable Long id, @RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(departmentService.addEmployeeToDepartment(id, employeeEmailWrapper), HttpStatus.OK);
	}
	
	
}
