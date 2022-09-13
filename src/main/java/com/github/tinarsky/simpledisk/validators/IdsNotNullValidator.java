package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.IdsNotNull;
import com.github.tinarsky.simpledisk.models.SystemItemImport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class IdsNotNullValidator
		implements ConstraintValidator<IdsNotNull, List<SystemItemImport>> {

	@Override
	public boolean isValid(List<SystemItemImport> items, ConstraintValidatorContext context) {
		return items.stream()
				.allMatch(item -> item.getId() != null);
	}

}
