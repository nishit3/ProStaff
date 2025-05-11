package com.prostaff.service_salary.controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service_salary.dto.AdminEmailWrapper;
import com.prostaff.service_salary.dto.EmployeeEmailWrapper;
import com.prostaff.service_salary.dto.FundOrderDetails;
import com.prostaff.service_salary.dto.FundsPaymentDetails;
import com.prostaff.service_salary.entity.SalaryLog;
import com.prostaff.service_salary.service.SalaryService;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping("/salary")
public class SalaryController {

	@Autowired
	private SalaryService salaryService;

	@PostMapping("/get-employee-salary")
	public ResponseEntity<Long> getEmployeeSalary(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Long>(salaryService.getEmployeeSalary(employeeEmailWrapper), HttpStatus.OK);
	}

	@PostMapping("/create-organization-fund/{organizationName}")
	@Hidden
	public ResponseEntity<Boolean> createOrganizationFund(@PathVariable String organizationName) {
		return new ResponseEntity<Boolean>(salaryService.createOrganizationFund(organizationName), HttpStatus.CREATED);
	}

	@PostMapping("/add-employee-salary/{salary}")
	@Hidden
	public ResponseEntity<Boolean> addEmployeeSalary(@RequestBody EmployeeEmailWrapper employeeEmailWrapper, @PathVariable Long salary) {
		return new ResponseEntity<Boolean>(salaryService.addEmployeeSalary(employeeEmailWrapper, salary), HttpStatus.OK);
	}

	@DeleteMapping("/delete-employee-salary")
	@Hidden
	public ResponseEntity<Boolean> deleteEmployeeSalary(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(salaryService.deleteEmployeeSalary(employeeEmailWrapper), HttpStatus.OK);
	}

	@PutMapping("/update-employee-salary/{newSalary}")
	public ResponseEntity<Boolean> updateEmployeeSalary(@PathVariable Long newSalary,
			@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<Boolean>(salaryService.updateEmployeeSalary(newSalary, employeeEmailWrapper),
				HttpStatus.OK);
	}

	@PostMapping("/get-current-organization-fund")
	public ResponseEntity<Long> getCurrentOrganizationFund(@RequestBody AdminEmailWrapper adminEmailWrapper) {
		return new ResponseEntity<Long>(salaryService.getCurrentOrganizationFund(adminEmailWrapper), HttpStatus.OK);
	}

	@PostMapping("/rollout-salary")
	public ResponseEntity<Boolean> rolloutSalary(@RequestPart AdminEmailWrapper adminEmailWrapper,
			@RequestPart List<String> employeeEmails) {
		return new ResponseEntity<Boolean>(salaryService.rollOutSalary(adminEmailWrapper, employeeEmails),
				HttpStatus.OK);
	}

	@PostMapping("/get-total-remuneration")
	public ResponseEntity<Long> getTotalRemuneration(@RequestPart AdminEmailWrapper adminEmailWrapper,
			@RequestPart List<String> employeeEmails) {
		return new ResponseEntity<Long>(salaryService.getTotalRemuneration(adminEmailWrapper, employeeEmails),
				HttpStatus.OK);
	}

	@PostMapping("/get-salary-logs")
	public ResponseEntity<List<SalaryLog>> getSalaryLogs(@RequestBody AdminEmailWrapper adminEmailWrapper) {
		return new ResponseEntity<List<SalaryLog>>(salaryService.getSalaryLogs(adminEmailWrapper), HttpStatus.OK);
	}
	
	@PostMapping("/add-funds/{amount}")
	public ResponseEntity<FundOrderDetails> addFunds(@PathVariable Long amount)
	{
		return new ResponseEntity<FundOrderDetails>(salaryService.addFunds(amount), HttpStatus.OK);
	}
	
	@PostMapping("/verify-fund-payment")
	public ResponseEntity<Boolean> verifyPayment(@RequestBody FundsPaymentDetails details)
	{
		return new ResponseEntity<Boolean>(salaryService.verifyPayment(details), HttpStatus.OK);
	}

}
