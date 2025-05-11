package com.prostaff.service.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service.employee.dto.EmployeeDetailsUpdate;
import com.prostaff.service.employee.dto.NewAdminDetails;
import com.prostaff.service.employee.dto.ResetPasswordTokenWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeRegistrationDetail;
import com.prostaff.service.employee.service.EmployeeService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/employee")
@Tag(name = "EmployeeController")

public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping("/add-employee")
	public ResponseEntity<Boolean> addEmployee(@Valid @RequestPart EmployeeRegistrationDetail employeeRegistrationDetail, @RequestPart MultipartFile profileImage){
		return new ResponseEntity<Boolean>(employeeService.addEmployee(employeeRegistrationDetail, profileImage),HttpStatus.OK);
	}
	
	@Hidden
	@PostMapping("/get-employees-basic-information")
	public ResponseEntity<List<EmployeeBasicInformation>> getEmployeesBasicInformation(@RequestBody List<String>employeeEmails){
		return new ResponseEntity<List<EmployeeBasicInformation>>(employeeService.getEmployeesBasicInformation(employeeEmails),HttpStatus.OK);
	}
	
	@PostMapping("/get-employee-information")
	public ResponseEntity<EmployeeInformation> getEmployeeInformation(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<EmployeeInformation>(employeeService.getEmployeeInformation(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/update-employee-profile-image")
	public ResponseEntity<Boolean> updateEmployeeProfileImage(@RequestPart MultipartFile newImage,@RequestPart EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<Boolean>(employeeService.updateEmployeeProfileImage(newImage, employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/update-employee")
	public ResponseEntity<Boolean> updateEmployee(@RequestBody EmployeeDetailsUpdate employeeDetailsUpdate){
		return new ResponseEntity<Boolean>(employeeService.updateEmployee(employeeDetailsUpdate),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-employee")
	public ResponseEntity<Boolean> deleteEmployee(@RequestBody EmployeeAdminEmailWrapper employeeAdminEmailWrapper){
		return new ResponseEntity<Boolean>(employeeService.deleteEmployee(employeeAdminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/is-employee-exist")
	public ResponseEntity<Boolean> isEmployeeExist(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<Boolean>(employeeService.isEmployeeExist(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-organization-employees")
	public ResponseEntity<List<EmployeeBasicInformation>> getAllOrganizationEmployees(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<EmployeeBasicInformation>>(employeeService.getAllOrganizationEmployees(adminEmailWrapper),HttpStatus.OK);
	}
	
	@Hidden
	@PostMapping("/send-reset-password-mail")
	public ResponseEntity<Boolean> sendResetPasswordLink(@RequestBody ResetPasswordTokenWrapper passwordTokenWrapper){
		return new ResponseEntity<Boolean>(employeeService.sendResetPasswordLink(passwordTokenWrapper), HttpStatus.OK);
	}
	
	@Hidden
	@PostMapping("/send-admin-welcome-mail")
	public ResponseEntity<Boolean> sendAdminWelcomeMail(@RequestBody NewAdminDetails details) {
		return new ResponseEntity<Boolean>(employeeService.sendAdminWelcomeMail(details), HttpStatus.OK);
	}
	
	
}
