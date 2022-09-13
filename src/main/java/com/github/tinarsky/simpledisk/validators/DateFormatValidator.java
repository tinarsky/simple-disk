package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.DateMustBeInISO8601;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class DateFormatValidator
		implements ConstraintValidator<DateMustBeInISO8601, String> {

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		try {
			Instant.parse(date);
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}
}
