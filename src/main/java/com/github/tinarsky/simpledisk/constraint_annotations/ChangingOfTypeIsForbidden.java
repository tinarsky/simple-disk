package com.github.tinarsky.simpledisk.constraint_annotations;

import com.github.tinarsky.simpledisk.validators.ChangingOfTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ChangingOfTypeValidator.class)
@Documented
public @interface ChangingOfTypeIsForbidden {

	String message() default "changing the item type is not allowed";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
