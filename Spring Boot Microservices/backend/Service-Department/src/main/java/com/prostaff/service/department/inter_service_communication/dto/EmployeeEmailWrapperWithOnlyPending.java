package com.prostaff.service.department.inter_service_communication.dto;

import lombok.Data;

@Data
public class EmployeeEmailWrapperWithOnlyPending {
	
	String employeeEmail; 
	Boolean onlyPending;
	
}
