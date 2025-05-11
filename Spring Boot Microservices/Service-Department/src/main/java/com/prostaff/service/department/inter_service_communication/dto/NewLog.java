package com.prostaff.service.department.inter_service_communication.dto;
import com.prostaff.service.department.inter_service_communication.enums.LogType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class NewLog {
	
   @Enumerated(EnumType.STRING)
   LogType type; 
   String adminEmail;  
   String message; 
   
}
