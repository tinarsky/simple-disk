package com.github.tinarsky.simpledisk.constraint_annotations;

import com.github.tinarsky.simpledisk.validators.FilesSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = FilesSizeValidator.class)
@Documented
public @interface FilesSizeMustBePositive {

	String message() default "file's size must be greater than zero";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
