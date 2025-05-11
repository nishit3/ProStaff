package com.prostaff.service.leave.request.inter_service_communication.dto;

import org.springframework.core.io.Resource;

import lombok.Data;

@Data
public class OrganizationData {
	
	String organizationName; 
    Resource image;
}
