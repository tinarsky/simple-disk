package com.github.tinarsky.simpledisk.services;

import com.github.tinarsky.simpledisk.constraint_annotations.DateMustBeInISO8601;
import com.github.tinarsky.simpledisk.domain.SystemItem;
import com.github.tinarsky.simpledisk.exceptions.BadRequestException;
import com.github.tinarsky.simpledisk.exceptions.NotFoundException;
import com.github.tinarsky.simpledisk.models.SystemItemImportRequest;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.repos.SystemItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class SystemItemService {
	private SystemItemRepo systemItemRepo;

	@Autowired
	public void setSystemItemRepo(SystemItemRepo systemItemRepo) {
		this.systemItemRepo = systemItemRepo;
	}

	public void save(@Valid SystemItemImportRequest importRequest) {
		List<SystemItem> items = new ArrayList<>();
		importRequest.getItems().forEach(item -> {
			var systemItem = new SystemItem.Builder()
					.withId(item.getId())
					.byUrl(item.getUrl())
					.hasParentById(item.getParentId())
					.hasType(item.getType())
					.size(item.getSize())
					.updatedIn(Instant.parse(importRequest.getUpdateDate()))
					.build();
			removeItemFromChildrenOfOldParent(systemItem);
			items.add(systemItem);
		});

		items.stream()
				.filter(item -> item.getType() == SystemItemType.FOLDER)
				.forEach(item ->
						systemItemRepo.findById(item.getId())
								.ifPresent(systemItem ->
										item.getChildren().addAll(systemItem.getChildren()))
				);

		systemItemRepo.saveAll(items);

		items.stream()
				.filter(item -> item.getParentId() != null)
				.forEach(item -> {
					/*the parent must be in the database,
					 otherwise the validation was failed*/
					var parentFromDb =
							systemItemRepo.findById(item.getParentId())
									.orElseThrow(BadRequestException::new);
					List<SystemItem> children = parentFromDb.getChildren();
					boolean childrenNotContainsItemWithSameId =
							children.stream()
									.noneMatch(systemItem ->
											Objects.equals(systemItem.getId(), item.getId()));
					if (childrenNotContainsItemWithSameId)
						children.add(item);
					updateParentDate(parentFromDb, item.getDate());
					systemItemRepo.save(parentFromDb);
				});
	}

	public void delete(String id, @Valid @DateMustBeInISO8601 String date) {
		SystemItem item = systemItemRepo.findById(id)
				.orElseThrow(NotFoundException::new);
		removeItemFromChildrenOfOldParent(item);
		systemItemRepo.deleteById(id);
	}

	public SystemItem getNodes(String id) {
		return systemItemRepo.findById(id)
				.orElseThrow(NotFoundException::new);
	}

	public long getFolderSize(SystemItem folder) {
		return folder.getChildren().stream()
				.mapToLong(item -> {
					if (item.getType() == SystemItemType.FOLDER)
						return getFolderSize(item);
					return item.getSize();
				})
				.sum();
	}

	private void removeItemFromChildrenOfOldParent(SystemItem item) {
		var itemFromDb = systemItemRepo.findById(item.getId());
		if (itemFromDb.isPresent()) {
			var oldItem = itemFromDb.get();
			var oldParentId = oldItem.getParentId();
			if (oldParentId != null) {
				//the parent must be in the database, otherwise the data is inconsistent
				var oldParent = systemItemRepo.findById(oldParentId)
						.orElseThrow();
				oldParent.getChildren().removeIf(systemItem ->
						Objects.equals(systemItem.getId(), item.getId()));
				updateParentDate(oldParent, item.getDate());
				systemItemRepo.save(oldParent);
			}
		}
	}

	private void updateParentDate(SystemItem parent, Instant date) {
		parent.setDate(date);
		if (parent.getParentId() != null) {
			var parentOfParent = systemItemRepo.findById(parent.getParentId())
					.orElseThrow();
			updateParentDate(parentOfParent, date);
		}
	}
}
