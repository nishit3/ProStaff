package com.prostaff.service_auth.inter_service_communication.dto;

import org.springframework.core.io.Resource;

import lombok.Data;

@Data
public class OrganizationData {
	
	String organizationName; 
    Resource image;
}
