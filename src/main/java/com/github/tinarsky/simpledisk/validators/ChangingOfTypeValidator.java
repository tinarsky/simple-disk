package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.ChangingOfTypeIsForbidden;
import com.github.tinarsky.simpledisk.models.SystemItemImport;
import com.github.tinarsky.simpledisk.repos.SystemItemRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ChangingOfTypeValidator implements ConstraintValidator<ChangingOfTypeIsForbidden, List<SystemItemImport>> {

	private SystemItemRepo systemItemRepo;

	@Autowired
	public void setSystemItemRepo(SystemItemRepo systemItemRepo) {
		this.systemItemRepo = systemItemRepo;
	}

	@Override
	public boolean isValid(List<SystemItemImport> items, ConstraintValidatorContext context) {
		return items.stream().allMatch(item -> {
			if(item.getId() == null)
				return false;
			var itemFromDb = systemItemRepo.findById(item.getId());
			return itemFromDb.isEmpty() || itemFromDb.get().getType() == item.getType();
		});
	}
}
