package com.prostaff.service.notification.inter_service_communication.dto;

import lombok.Data;

@Data
public class TeamBasicInformation {

	 Long id;
	 String name; 
	 String description;
	 Long employeeCount;
}
