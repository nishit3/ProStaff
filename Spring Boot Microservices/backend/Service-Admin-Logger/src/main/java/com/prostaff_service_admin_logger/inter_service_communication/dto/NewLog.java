package com.prostaff_service_admin_logger.inter_service_communication.dto;

import com.prostaff_service_admin_logger.inter_service_communication.enums.LogType;

import lombok.Data;

@Data
public class NewLog {
	
   LogType type; 
   String adminEmail;  
   String message; 
   
}
