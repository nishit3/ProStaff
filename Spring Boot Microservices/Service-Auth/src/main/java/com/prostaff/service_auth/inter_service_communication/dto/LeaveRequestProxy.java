package com.prostaff.service_auth.inter_service_communication.dto;

import java.sql.Date;

import com.prostaff.service_auth.inter_service_communication.enums.LeaveRequestStatus;
import com.prostaff.service_auth.inter_service_communication.enums.LeaveRequestType;

import lombok.Data;


@Data
public class LeaveRequestProxy {
	
	Integer id;
	String employee_email; 
	Date requestDate; 
	Date leaveDate; 
	String reason; 
	LeaveRequestStatus status;  
	LeaveRequestType type; 
	
}
