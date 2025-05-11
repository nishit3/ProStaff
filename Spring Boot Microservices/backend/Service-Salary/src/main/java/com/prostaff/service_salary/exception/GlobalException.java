package com.prostaff.service_salary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(value = EmployeeNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleEmployeeNotFoundException(EmployeeNotFoundException e) {
		return new ErrorResponse("Employee not found",1);
	}
	
	@ExceptionHandler(value = EmployeeAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleEmployeeAlreadyExistsException(EmployeeAlreadyExistsException e) {
		return new ErrorResponse("Employee already exist",2);
	}
	
	@ExceptionHandler(value = InsufficientOrganizationFundException.class)
	@ResponseStatus(value = HttpStatus.INSUFFICIENT_STORAGE)
	public ErrorResponse handleInsufficientOrganizationFundException(InsufficientOrganizationFundException e) {
		return new ErrorResponse("Insufficient Organization fund",14);
	}
	
	@ExceptionHandler(value = OrganizationNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleOrganizationNotFoundException(OrganizationNotFoundException e) {
		return new ErrorResponse("Organization not found",15);
	}
	
	
}
