package com.prostaff.service.department.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException{
	
	@ExceptionHandler(value = DepartmentNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorResponse handleDepartmentNotFound(DepartmentNotFoundException e) {
		return new ErrorResponse("Department not found",4);
	}
	
	@ExceptionHandler(value = DepartmentAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleDepartmentAlreadyExist(DepartmentAlreadyExistsException e) {
		return new ErrorResponse("Department already exist",5);
	}
}
