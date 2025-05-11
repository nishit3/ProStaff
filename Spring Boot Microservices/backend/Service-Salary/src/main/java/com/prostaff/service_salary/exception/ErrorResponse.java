package com.prostaff.service_salary.exception;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Component
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ErrorResponse {
	private String errorMessage;
	private int errorCode;
}