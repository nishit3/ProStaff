package com.prostaff.service_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewAdminAndOrganizationDetails {
	String adminEmail;
	String organizationName;
}
