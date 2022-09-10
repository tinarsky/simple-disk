package com.github.tinarsky.simpledisk.exception_handlers;

import com.github.tinarsky.simpledisk.exceptions.BadRequestException;
import com.github.tinarsky.simpledisk.exceptions.NotFoundException;
import com.github.tinarsky.simpledisk.models.ClientError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
	private ClientError badRequestError;
	private ClientError notFoundError;

	@Autowired
	public void setBadRequestError(
			@Qualifier("badRequestErrorBean") ClientError badRequestError) {
		this.badRequestError = badRequestError;
	}

	@Autowired
	public void setNotFoundError(
			@Qualifier("notFoundErrorBean") ClientError notFoundError) {
		this.notFoundError = notFoundError;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	public ClientError handleBadRequestException(BadRequestException e) {
		return badRequestError;
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ClientError handleNotFoundException(NotFoundException e) {
		return notFoundError;
	}
}
