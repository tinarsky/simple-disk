package com.github.tinarsky.simpledisk.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.serializers.SystemItemJsonSerializer;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonSerialize(using = SystemItemJsonSerializer.class)
public class SystemItem {
	@Id
	@Basic(optional = false)
	private String id;

	private String url;

	@Basic(optional = false)
	private Instant date;

	private String parentId;

	@Basic(optional = false)
	private SystemItemType type;

	private Long size;

	@OneToMany(cascade = CascadeType.REMOVE)
	private List<SystemItem> children;

	public String getId() {
		return id;
	}

	public SystemItemType getType() {
		return type;
	}

	public List<SystemItem> getChildren() {
		return children;
	}

	public String getParentId() {
		return parentId;
	}

	public SystemItem() {
	}

	public String getUrl() {
		return this.url;
	}

	public Instant getDate() {
		return this.date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public static class Builder {
		private final SystemItem preBuildItem;

		public Builder() {
			preBuildItem = new SystemItem();
		}

		public SystemItem build() {
			return preBuildItem;
		}

		public Builder withId(String id) {
			preBuildItem.id = id;
			return this;
		}

		public Builder byUrl(String url) {
			preBuildItem.url = url;
			return this;
		}

		public Builder updatedIn(Instant date) {
			preBuildItem.date = date;
			return this;
		}

		public Builder hasParentById(String parentId) {
			preBuildItem.parentId = parentId;
			return this;
		}

		public Builder hasType(SystemItemType type) {
			preBuildItem.type = type;
			if (type == SystemItemType.FOLDER)
				preBuildItem.children = new ArrayList<>();
			return this;
		}

		public Builder size(Long size) {
			preBuildItem.size = size;
			return this;
		}
	}
}
