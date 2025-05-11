package com.prostaff.service_organization.inter_service_communication.dto;

import java.sql.Date;

import com.prostaff.service_organization.inter_service_communication.enums.LeaveRequestStatus;
import com.prostaff.service_organization.inter_service_communication.enums.LeaveRequestType;

public class LeaveRequestProxy {
	
	Integer id;
	String employee_email; 
	Date requestDate; 
	Date leaveDate; 
	String reason; 
	LeaveRequestStatus status;  
	LeaveRequestType type; 
	
}
