package com.github.tinarsky.simpledisk.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.tinarsky.simpledisk.models.SystemItemHistoryResponse;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class HistoryResponseJsonSerializer extends JsonSerializer<SystemItemHistoryResponse> {
	@Override
	public void serialize(SystemItemHistoryResponse response, JsonGenerator jsonGenerator,
						  SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeObjectField("items", response.getItems());
		jsonGenerator.writeEndObject();
	}
}

