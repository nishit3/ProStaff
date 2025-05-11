package com.prostaff.service.designation.inter_service_communication.dto;

import org.springframework.core.io.Resource;

import lombok.Data;

@Data
public class OrganizationData {
	
	String organizationName; 
    Resource image;
}
