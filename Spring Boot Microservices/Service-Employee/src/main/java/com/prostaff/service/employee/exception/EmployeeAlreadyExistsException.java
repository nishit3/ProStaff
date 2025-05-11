package com.prostaff.service.employee.exception;

public class EmployeeAlreadyExistsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String message;
	public int errorCode;
}
