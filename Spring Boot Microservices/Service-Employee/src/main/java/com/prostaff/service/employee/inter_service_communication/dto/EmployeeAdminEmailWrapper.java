package com.prostaff.service.employee.inter_service_communication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeAdminEmailWrapper {
	
	String employeeEmail; 
	String adminEmail;  
	
}
