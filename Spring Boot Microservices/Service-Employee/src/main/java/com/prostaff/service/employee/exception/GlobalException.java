package com.prostaff.service.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalException {

	@ExceptionHandler(value = EmployeeNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleEmployeeNotFound(EmployeeNotFoundException e) {
		return new ErrorResponse("Employee not found",1);
	}
	
	@ExceptionHandler(value = EmployeeAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleEmployeeAlreadyExists(EmployeeAlreadyExistsException e) {
		return new ErrorResponse("Employee already exists",2);
	}
}
