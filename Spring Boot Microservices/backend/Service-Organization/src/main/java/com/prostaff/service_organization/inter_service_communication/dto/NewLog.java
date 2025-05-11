package com.prostaff.service_organization.inter_service_communication.dto;

import com.prostaff.service_organization.inter_service_communication.enums.LogType;

import lombok.Data;

@Data
public class NewLog {
	
   LogType type; 
   String adminEmail;  
   String message; 
   
}
