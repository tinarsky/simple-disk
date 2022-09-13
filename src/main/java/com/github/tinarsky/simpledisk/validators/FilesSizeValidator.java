package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.FilesSizeMustBePositive;
import com.github.tinarsky.simpledisk.models.SystemItemImport;
import com.github.tinarsky.simpledisk.models.SystemItemType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class FilesSizeValidator
		implements ConstraintValidator<FilesSizeMustBePositive, List<SystemItemImport>> {

	@Override
	public boolean isValid(List<SystemItemImport> items, ConstraintValidatorContext context) {
		return items.stream()
				.allMatch(item -> (item.getType() == SystemItemType.FOLDER) ||
						(item.getSize() != null && item.getSize() > 0));
	}

}
