package com.github.tinarsky.simpledisk.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.tinarsky.simpledisk.domain.SystemItemHistoryUnit;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class HistoryUnitJsonSerializer extends JsonSerializer<SystemItemHistoryUnit> {
	@Override
	public void serialize(SystemItemHistoryUnit historyUnit, JsonGenerator jsonGenerator,
						  SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("id", historyUnit.getItemId());
		jsonGenerator.writeStringField("url", historyUnit.getUrl());
		jsonGenerator.writeStringField("parentId", historyUnit.getParentId());
		jsonGenerator.writeStringField("type", historyUnit.getType().toString());
		jsonGenerator.writeNumberField("size", historyUnit.getSize());
		jsonGenerator.writeStringField("date", historyUnit.getDate().toString());
		jsonGenerator.writeEndObject();
	}
}

