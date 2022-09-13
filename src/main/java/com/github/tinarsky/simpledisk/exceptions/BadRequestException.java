package com.github.tinarsky.simpledisk.exceptions;

public class BadRequestException extends RuntimeException {
	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException() {
		super("Validation Failed");
	}
}
