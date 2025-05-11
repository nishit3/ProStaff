package com.prostaff_service_admin_logger.inter_service_communication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeAdminEmailWrapper {
	
	String employeeEmail; 
	String adminEmail;  
	
}
