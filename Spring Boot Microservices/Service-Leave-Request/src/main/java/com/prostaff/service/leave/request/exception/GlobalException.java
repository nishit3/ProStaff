package com.prostaff.service.leave.request.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(value = LeaveRequestNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleLeaveRequestNotFound(LeaveRequestNotFoundException e) {
		return new ErrorResponse("Leave request not found",3);
	}
	
	@ExceptionHandler(value = LeaveRequestAlreadyExist.class)
	@ResponseStatus(value = HttpStatus.FOUND)
	public ErrorResponse handleLeaveRequestAlreadyExist(LeaveRequestAlreadyExist e) {
		return new ErrorResponse("Leave request already exist", 13);
	}
}
