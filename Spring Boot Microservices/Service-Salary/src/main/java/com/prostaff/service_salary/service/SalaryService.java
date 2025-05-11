package com.prostaff.service_salary.service;

import java.util.List;

import com.prostaff.service_salary.dto.AdminEmailWrapper;
import com.prostaff.service_salary.dto.EmployeeEmailWrapper;
import com.prostaff.service_salary.dto.FundOrderDetails;
import com.prostaff.service_salary.dto.FundsPaymentDetails;
import com.prostaff.service_salary.entity.SalaryLog;

public interface SalaryService {

	// write this comment in service interface “razor pay create order and verify payment methods left” 
	
	public Long getEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean createOrganizationFund(String organizationName);
	public Boolean addEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper, Long salary);
	public Boolean deleteEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean updateEmployeeSalary(Long newSalary, EmployeeEmailWrapper employeeEmailWrapper);
	public Long getCurrentOrganizationFund(AdminEmailWrapper adminEmailWrapper);
	public Boolean rollOutSalary(AdminEmailWrapper adminEmailWrapper,List<String>employeeEmails);
	public Long getTotalRemuneration(AdminEmailWrapper adminEmailWrapper, List<String>employeeEmails);
	public List<SalaryLog> getSalaryLogs(AdminEmailWrapper adminEmailWrapper);
	public FundOrderDetails addFunds(Long amount);
	public Boolean verifyPayment(FundsPaymentDetails details); 
}
