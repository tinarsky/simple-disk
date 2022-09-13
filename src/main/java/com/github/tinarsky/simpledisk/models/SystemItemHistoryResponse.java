package com.github.tinarsky.simpledisk.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tinarsky.simpledisk.domain.SystemItemHistoryUnit;
import com.github.tinarsky.simpledisk.serializers.HistoryResponseJsonSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonSerialize(using = HistoryResponseJsonSerializer.class)
public class SystemItemHistoryResponse {
	private final List<SystemItemHistoryUnit> items;

	public List<SystemItemHistoryUnit> getItems() {
		return items;
	}

	public SystemItemHistoryResponse(Collection<SystemItemHistoryUnit> items) {
		this.items = new ArrayList<>(items);
	}
}
