package com.github.tinarsky.simpledisk.validators;

import com.github.tinarsky.simpledisk.constraint_annotations.ElementsParentMustExistsAndBeFolder;
import com.github.tinarsky.simpledisk.models.SystemItemImport;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.repos.SystemItemRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class ElementsParentValidator implements ConstraintValidator<ElementsParentMustExistsAndBeFolder, List<SystemItemImport>> {

	private SystemItemRepo systemItemRepo;

	@Autowired
	public void setSystemItemRepo(SystemItemRepo systemItemRepo) {
		this.systemItemRepo = systemItemRepo;
	}

	@Override
	public boolean isValid(List<SystemItemImport> items, ConstraintValidatorContext context) {
		return items.stream().allMatch(item -> {
			var parentId = item.getParentId();
			if(parentId == null)
				return true;
			var parentFromDb = systemItemRepo.findById(parentId);
			if (parentFromDb.isPresent() &&
					parentFromDb.get().getType() == SystemItemType.FOLDER) {
				return true;
			}
			var parentFromRequest = items.stream()
					.filter(itemImport -> Objects.equals(itemImport.getId(), parentId))
					.findAny();
			return (parentFromRequest.isPresent() &&
					parentFromRequest.get().getType() == SystemItemType.FOLDER);
		});
	}
}
