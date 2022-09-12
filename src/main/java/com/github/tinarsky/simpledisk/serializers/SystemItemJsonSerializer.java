package com.github.tinarsky.simpledisk.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.tinarsky.simpledisk.domain.SystemItem;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.services.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SystemItemJsonSerializer extends JsonSerializer<SystemItem> {
	private SystemItemService systemItemService;

	@Autowired
	public void setSystemItemService(SystemItemService systemItemService) {
		this.systemItemService = systemItemService;
	}

	@Override
	public void serialize(SystemItem item, JsonGenerator jsonGenerator,
						  SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("id", item.getId());
		jsonGenerator.writeStringField("url", item.getUrl());
		jsonGenerator.writeStringField("type", item.getType().toString());
		jsonGenerator.writeStringField("parentId", item.getParentId());
		jsonGenerator.writeStringField("date", item.getDate().toString());

		if (item.getType() == SystemItemType.FOLDER) {
			jsonGenerator.writeNumberField("size",
					systemItemService.getFolderSize(item));
			jsonGenerator.writeObjectField("children", item.getChildren());
		} else {
			jsonGenerator.writeNumberField("size", item.getSize());
			jsonGenerator.writeNullField("children");
		}

		jsonGenerator.writeEndObject();
	}
}

