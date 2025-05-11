package com.prostaff.service_auth.inter_service_communication.dto;

import java.sql.Date;

import lombok.Data;
@Data
public class GrantedLeaveRequest {
	
	String employeeEmail; 
	String adminEmail; 
	Date date; 
	
}
