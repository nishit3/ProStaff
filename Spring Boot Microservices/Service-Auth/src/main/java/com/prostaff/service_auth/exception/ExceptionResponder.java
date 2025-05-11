package com.prostaff.service_auth.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponder {
	int errorCode;
	String errorMessage;
}
