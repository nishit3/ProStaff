package com.prostaff.service_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAdmin {
	String adminEmail;
	String newAdminEmail;
	String newAdminFullName;
}
