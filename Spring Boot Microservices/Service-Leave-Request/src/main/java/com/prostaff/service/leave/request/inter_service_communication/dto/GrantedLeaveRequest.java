package com.prostaff.service.leave.request.inter_service_communication.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class GrantedLeaveRequest {
	
	String employeeEmail; 
	String adminEmail; 
	Date date; 
	
}
