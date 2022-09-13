package com.github.tinarsky.simpledisk.models;

import com.github.tinarsky.simpledisk.constraint_annotations.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public class SystemItemImportRequest {
	@UniqueIds
	@IdsNotNull
	@FoldersUrlsMustBeNull
	@FilesUrlsSize
	@FoldersSizeMustBeNull
	@FilesSizeMustBePositive
	@ChangingOfTypeIsForbidden
	@ElementsParentMustExistsAndBeFolder
	private List<SystemItemImport> items;
	@DateMustBeInISO8601
	private String updateDate;

	public List<SystemItemImport> getItems() {
		return items;
	}

	public void setItems(List<SystemItemImport> items) {
		this.items = items;
	}

	public String getUpdateDate() {
		return updateDate;
	}
}
