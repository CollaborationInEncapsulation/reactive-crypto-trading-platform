package com.example.demo.controller.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebSocketMessageMapper {
	private final ObjectMapper mapper;

	public WebSocketMessageMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public String encode(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Message<Message.Trade> decode(String message) {
		try {
			return mapper.readValue(message, new TypeReference<Message<Message.Trade>>(){});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}