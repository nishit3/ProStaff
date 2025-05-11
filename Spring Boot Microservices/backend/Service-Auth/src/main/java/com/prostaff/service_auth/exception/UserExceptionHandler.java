package com.prostaff.service_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
	
	@ResponseStatus(code = HttpStatus.FOUND)
	@ExceptionHandler(exception = OrganizationAlreadyExistsException.class)
	public ExceptionResponder organizationAlreadyExists(OrganizationAlreadyExistsException e)
	{
		return new ExceptionResponder(0, "Organization Already Exists");
	}
	
	@ResponseStatus(code = HttpStatus.FOUND)
	@ExceptionHandler(exception = AdminAlreadyExistsException.class)
	public ExceptionResponder adminAlreadyExists(AdminAlreadyExistsException e)
	{
		return new ExceptionResponder(20, "Admin Already Exists");
	}
}
