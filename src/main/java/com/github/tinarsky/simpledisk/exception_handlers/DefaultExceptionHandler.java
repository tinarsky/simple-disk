package com.github.tinarsky.simpledisk.exception_handlers;

import com.github.tinarsky.simpledisk.exceptions.BadRequestException;
import com.github.tinarsky.simpledisk.exceptions.NotFoundException;
import com.github.tinarsky.simpledisk.models.ClientError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
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
		return new ClientError(400, e.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ClientError handleNotFoundException(NotFoundException e) {
		return notFoundError;
	}

	@NonNull
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatus status, @NonNull WebRequest request) {
		return new ResponseEntity<>(badRequestError, HttpStatus.BAD_REQUEST);
	}

	@NonNull
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			@NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatus status, @NonNull WebRequest request) {
		return new ResponseEntity<>(badRequestError, HttpStatus.BAD_REQUEST);
	}

	@NonNull
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(
			@NonNull MissingPathVariableException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatus status, @NonNull WebRequest request) {
		return new ResponseEntity<>(badRequestError, HttpStatus.BAD_REQUEST);
	}

	@NonNull
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
			@NonNull NoHandlerFoundException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatus status, @NonNull WebRequest request) {
		return new ResponseEntity<>(badRequestError, HttpStatus.BAD_REQUEST);
	}

	@NonNull
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			@NonNull HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatus status, @NonNull WebRequest request) {
		return new ResponseEntity<>(badRequestError, HttpStatus.BAD_REQUEST);
	}
}
