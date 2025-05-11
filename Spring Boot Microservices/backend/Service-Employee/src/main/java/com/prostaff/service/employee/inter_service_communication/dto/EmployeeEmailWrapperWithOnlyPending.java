package com.prostaff.service.employee.inter_service_communication.dto;

import lombok.Data;

@Data
public class EmployeeEmailWrapperWithOnlyPending {
	
	String employeeEmail; 
	Boolean onlyPending;
	
}
