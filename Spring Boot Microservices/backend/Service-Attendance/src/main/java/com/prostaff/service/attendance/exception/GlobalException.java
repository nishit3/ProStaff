package com.prostaff.service.attendance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GlobalException {

	@ExceptionHandler(value = EmployeeNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleEmployeeNotFoundException(EmployeeNotFoundException e) {
		return new ErrorResponse("Employee not found", 1);
	}
	
	@ExceptionHandler(value = HolidayNotFoundException .class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleHolidayNotFoundException(HolidayNotFoundException  e) {
		return new ErrorResponse("Holiday not found", 13);
	}
	
}
