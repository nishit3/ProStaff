package com.prostaff.service.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordTokenWrapper {
	String email;
	String fullName;
	String token;
}
