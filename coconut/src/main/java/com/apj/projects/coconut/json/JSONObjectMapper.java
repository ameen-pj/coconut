package com.apj.projects.coconut.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONObjectMapper {

	private static ObjectMapper jsonObjectMapper;

	private JSONObjectMapper() {
		jsonObjectMapper = new ObjectMapper();
		// Configuration
		jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jsonObjectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		jsonObjectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	}

	public static ObjectMapper getMapper() {
		if (jsonObjectMapper == null) {
			new JSONObjectMapper();
		}
		return jsonObjectMapper;
	}

}
