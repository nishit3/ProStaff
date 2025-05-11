package com.prostaff.service.designation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {


	@ExceptionHandler(value = DesignationtNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleDesignationNotFound(DesignationtNotFoundException e) {
		return new ErrorResponse("Designation not found",6);
	}
	
	@ExceptionHandler(value = DesignationAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleDesignationAlreadyExist(DesignationAlreadyExistsException e) {
		return new ErrorResponse("Designation already exists",7);
	}
}
