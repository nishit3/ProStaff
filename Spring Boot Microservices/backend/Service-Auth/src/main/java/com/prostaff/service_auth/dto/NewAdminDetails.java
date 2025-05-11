package com.prostaff.service_auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewAdminDetails {
	String email;
	String password;
	String fullName;
}
