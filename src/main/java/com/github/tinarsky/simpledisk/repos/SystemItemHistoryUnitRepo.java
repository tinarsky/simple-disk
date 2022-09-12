package com.github.tinarsky.simpledisk.repos;

import com.github.tinarsky.simpledisk.domain.SystemItemHistoryUnit;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SystemItemHistoryUnitRepo
		extends JpaRepository<SystemItemHistoryUnit, Long> {
	List<SystemItemHistoryUnit> findByTypeAndDateGreaterThanEqualAndDateLessThanEqual(
			SystemItemType type, Instant startDate, Instant endDate);

	List<SystemItemHistoryUnit> findByItemIdAndDateGreaterThanEqualAndDateLessThan(
			String itemId, Instant startDate, Instant endDate);

	Optional<SystemItemHistoryUnit> findByItemIdAndUrlAndParentIdAndTypeAndSizeAndDate(
			String itemId, String url, String parentId, SystemItemType type, Long size, Instant date);

	@Transactional
	void deleteAllByItemId(String itemId);
}
