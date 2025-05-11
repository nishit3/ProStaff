package com.prostaff.service.team.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(value = TeamNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorResponse handleTeamNotFound(TeamNotFoundException e) {
		return new ErrorResponse("Team not found", 8);
	}

	@ExceptionHandler(value = TeamAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleTeamAlreadyExists(TeamAlreadyExistsException e) {
		return new ErrorResponse("Team already exists", 9);
	}

}
