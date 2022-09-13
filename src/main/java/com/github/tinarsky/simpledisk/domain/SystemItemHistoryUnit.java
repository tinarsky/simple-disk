package com.github.tinarsky.simpledisk.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.serializers.HistoryUnitJsonSerializer;

import javax.persistence.*;
import java.time.Instant;

@Entity
@JsonSerialize(using = HistoryUnitJsonSerializer.class)
public class SystemItemHistoryUnit {

	@Id
	@GeneratedValue
	private Long unitId;

	@Basic(optional = false)
	private String itemId;

	private String url;

	private String parentId;

	@Basic(optional = false)
	private SystemItemType type;

	private Long size;

	@Basic(optional = false)
	private Instant date;

	public String getItemId() {
		return itemId;
	}

	public String getUrl() {
		return url;
	}

	public String getParentId() {
		return parentId;
	}

	public SystemItemType getType() {
		return type;
	}

	public Long getSize() {
		return size;
	}

	public Instant getDate() {
		return date;
	}

	public SystemItemHistoryUnit(SystemItem item) {
		this.itemId = item.getId();
		this.url = item.getUrl();
		this.parentId = item.getParentId();
		this.type = item.getType();
		this.size = item.getSize();
		this.date = item.getDate();
	}

	public SystemItemHistoryUnit() {

	}
}
