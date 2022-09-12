package com.github.tinarsky.simpledisk.constraint_annotations;

import com.github.tinarsky.simpledisk.validators.FoldersUrlsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = FoldersUrlsValidator.class)
@Documented
public @interface FoldersUrlsMustBeNull {

	String message() default "folders urls must be null";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
