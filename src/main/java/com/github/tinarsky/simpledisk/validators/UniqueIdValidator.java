package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.UniqueIds;
import com.github.tinarsky.simpledisk.models.SystemItemImport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueIdValidator
		implements ConstraintValidator<UniqueIds, List<SystemItemImport>> {

	@Override
	public boolean isValid(List<SystemItemImport> items, ConstraintValidatorContext context) {
		return items.stream()
				.map(SystemItemImport::getId)
				.distinct().count() == items.size();
	}

}
