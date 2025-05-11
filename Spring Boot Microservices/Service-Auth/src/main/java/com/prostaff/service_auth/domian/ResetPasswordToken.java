package com.prostaff.service_auth.domian;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String token;
	String email;
	LocalDateTime expirationTime;
	
}
