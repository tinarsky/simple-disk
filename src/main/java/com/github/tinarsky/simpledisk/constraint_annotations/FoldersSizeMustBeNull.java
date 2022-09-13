package com.github.tinarsky.simpledisk.constraint_annotations;

import com.github.tinarsky.simpledisk.validators.FoldersSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = FoldersSizeValidator.class)
@Documented
public @interface FoldersSizeMustBeNull {

	String message() default "folder's size must be null";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
