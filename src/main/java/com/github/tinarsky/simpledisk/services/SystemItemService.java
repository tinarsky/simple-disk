package com.github.tinarsky.simpledisk.services;

import com.github.tinarsky.simpledisk.constraint_annotations.DateMustBeInISO8601;
import com.github.tinarsky.simpledisk.domain.SystemItem;
import com.github.tinarsky.simpledisk.domain.SystemItemHistoryUnit;
import com.github.tinarsky.simpledisk.exceptions.BadRequestException;
import com.github.tinarsky.simpledisk.exceptions.NotFoundException;
import com.github.tinarsky.simpledisk.models.SystemItemHistoryResponse;
import com.github.tinarsky.simpledisk.models.SystemItemImportRequest;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.repos.SystemItemHistoryUnitRepo;
import com.github.tinarsky.simpledisk.repos.SystemItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class SystemItemService {
	private SystemItemRepo systemItemRepo;
	private SystemItemHistoryUnitRepo systemItemHistoryUnitRepo;

	//ids of folders that size must be recalculated
	private final Set<String> idsOfFoldersToUpdate = new HashSet<>();
	private Instant lastUpdateDate;

	@Autowired
	public void setSystemItemRepo(SystemItemRepo systemItemRepo) {
		this.systemItemRepo = systemItemRepo;
	}

	@Autowired
	public void setSystemItemHistoryUnitRepo(SystemItemHistoryUnitRepo systemItemHistoryUnitRepo) {
		this.systemItemHistoryUnitRepo = systemItemHistoryUnitRepo;
	}

	public void save(@Valid SystemItemImportRequest importRequest) {
		List<SystemItem> items = new ArrayList<>();
		lastUpdateDate = Instant.parse(importRequest.getUpdateDate());

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
			if (systemItem.getType() == SystemItemType.FILE)
				saveHistoryUnitIfNoDuplicates(systemItem);
		});

		addRemainingChildrenToFolders(items);

		systemItemRepo.saveAll(items);

		addItemsToChildrenOfNewParent(items);

		updateFolders();
	}

	public void delete(String id, @Valid @DateMustBeInISO8601 String date) {
		SystemItem item = systemItemRepo.findById(id)
				.orElseThrow(NotFoundException::new);
		lastUpdateDate = Instant.parse(date);
		removeItemFromChildrenOfOldParent(item);
		updateFolders();
		deleteItemAndHisChildrenHistory(item);
		systemItemRepo.deleteById(id);
	}

	public SystemItem getNodes(String id) {
		return systemItemRepo.findById(id)
				.orElseThrow(NotFoundException::new);
	}

	public SystemItemHistoryResponse getUpdates(
			@Valid @DateMustBeInISO8601 String date) {
		Instant endDate = Instant.parse(date);
		Instant startDate;
		try {
			startDate = endDate.minus(24, ChronoUnit.HOURS);
		} catch (DateTimeException e) {
			throw new BadRequestException();
		}

		Collection<SystemItemHistoryUnit> lastHistoryUnits =
				getLastUpdatesOfAllFilesForPeriod(endDate, startDate);

		return new SystemItemHistoryResponse(lastHistoryUnits);
	}

	public SystemItemHistoryResponse getItemHistory(
			String id,
			@Valid @DateMustBeInISO8601 String dateStart,
			@Valid @DateMustBeInISO8601 String dateEnd) {
		systemItemRepo.findById(id)
				.orElseThrow(NotFoundException::new);
		Instant startDate = Instant.parse(dateStart);
		Instant endDate = Instant.parse(dateEnd);
		List<SystemItemHistoryUnit> historyUnits = systemItemHistoryUnitRepo
				.findByItemIdAndDateGreaterThanEqualAndDateLessThan(
						id, startDate, endDate);
		return new SystemItemHistoryResponse(historyUnits);
	}

	private void deleteItemAndHisChildrenHistory(SystemItem item) {
		systemItemHistoryUnitRepo.deleteAllByItemId(item.getId());
		if (item.getType() == SystemItemType.FILE)
			return;

		item.getChildren().forEach(this::deleteItemAndHisChildrenHistory);
	}

	//calculates folder size recursive, updates size and date
	private long calculateAndUpdateFolderSize(SystemItem folder) {
		long size = folder.getChildren().stream()
				.mapToLong(item -> {
					if (item.getType() == SystemItemType.FOLDER &&
							idsOfFoldersToUpdate.contains(item.getId()))
						return calculateAndUpdateFolderSize(item);
					return item.getSize();
				})
				.sum();

		idsOfFoldersToUpdate.remove(folder.getId());
		folder.setSize(size);
		folder.setDate(lastUpdateDate);
		systemItemRepo.save(folder);
		saveHistoryUnitIfNoDuplicates(folder);

		return size;
	}

	private void addRemainingChildrenToFolders(List<SystemItem> items) {
		items.stream()
				.filter(item -> item.getType() == SystemItemType.FOLDER)
				.forEach(item -> {
							systemItemRepo.findById(item.getId())
									.ifPresent(systemItem ->
											item.getChildren().addAll(systemItem.getChildren()));
							idsOfFoldersToUpdate.add(item.getId());
						}
				);
	}

	private void addItemsToChildrenOfNewParent(List<SystemItem> items) {
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
					addParentIdInSetToUpdate(parentFromDb);
					systemItemRepo.save(parentFromDb);
				});
	}

	@NonNull
	private Collection<SystemItemHistoryUnit> getLastUpdatesOfAllFilesForPeriod(
			Instant endDate, Instant startDate) {
		List<SystemItemHistoryUnit> historyUnits = systemItemHistoryUnitRepo
				.findByTypeAndDateGreaterThanEqualAndDateLessThanEqual(
						SystemItemType.FILE, startDate, endDate);
		return historyUnits.stream()
				.collect(Collectors.toMap(
						SystemItemHistoryUnit::getItemId,
						Function.identity(),
						(unit1, unit2) ->
								(unit1.getDate().isAfter(unit2.getDate()) ?
										unit1 : unit2)))
				.values();
	}

	private void saveHistoryUnitIfNoDuplicates(SystemItem item) {
		boolean noDuplicates = systemItemHistoryUnitRepo.findByItemIdAndUrlAndParentIdAndTypeAndSizeAndDate(
				item.getId(), item.getUrl(), item.getParentId(), item.getType(), item.getSize(), item.getDate()
		).isEmpty();
		if (noDuplicates)
			systemItemHistoryUnitRepo.save(new SystemItemHistoryUnit(item));
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
				addParentIdInSetToUpdate(oldParent);
				systemItemRepo.save(oldParent);
			}
		}
	}

	//adds in set to update parent and whole hierarchy of his parents recursive
	private void addParentIdInSetToUpdate(SystemItem parent) {
		idsOfFoldersToUpdate.add(parent.getId());
		if (parent.getParentId() != null) {
			var parentOfParent = systemItemRepo.findById(parent.getParentId())
					.orElseThrow();
			addParentIdInSetToUpdate(parentOfParent);
		}
	}

	private void updateFolders() {
		new ArrayList<>(idsOfFoldersToUpdate)
				.forEach(id -> {
					if (!idsOfFoldersToUpdate.contains(id))
						return;
					var folderFromDb = systemItemRepo.findById(id);
					if (folderFromDb.isEmpty()) {
						idsOfFoldersToUpdate.remove(id);
						return;
					}
					var folder = folderFromDb.get();
					calculateAndUpdateFolderSize(folder);
				});
	}
}
