package com.prostaff.service_auth.domian;

import com.prostaff.service_auth.inter_service_communication.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	String email;
	String password;
	@Enumerated(EnumType.STRING)
	Role role; 
	String organizationName; 
	String fullName;
	
}
