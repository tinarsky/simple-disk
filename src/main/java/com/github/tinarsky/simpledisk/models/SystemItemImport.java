package com.github.tinarsky.simpledisk.models;

public class SystemItemImport {
	private String id;
	private String url;
	private String parentId;
	private SystemItemType type;
	private Long size;

	public String getId() {
		return id;
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
}
