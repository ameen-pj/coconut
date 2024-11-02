package com.apj.projects.coconut.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;

public class HTTPResponse {

	private String HTTPVersion;
	private HTTPStatusCode httpStatusCode;
	private HashMap<String, List<String>> headers = new HashMap<>();
	private Optional<?> body;

	// Setters

	public void setHTTPVersion(String version) {
		this.HTTPVersion = version;
	}

	public void setStatus(HTTPStatusCode statusCode) {
		this.httpStatusCode = statusCode;
	}

	public void setContentType(HTTPContentTypes contentType) {
		this.headers.put("Content-Type", Arrays.asList(contentType.getName()));
	}

	public void setContentLength(long contentLength) {
		this.headers.put("Content-Length", Arrays.asList(Long.toString(contentLength)));
	}

	public void setHeaders(HashMap<String, List<String>> headers) {
		this.headers = headers;
	}

	public <T> void setHeader(String key, String value) {
		headers.put(key, Arrays.asList(value));
	}

	public <T> void setBody(T body) {
		this.body = Optional.ofNullable(body);
	}

	// Getters

	public String getHTTPVersion() {
		return this.HTTPVersion;
	}

	public HTTPStatusCode getStatus() {
		return this.httpStatusCode;
	}

	public HashMap<String, List<String>> getHeaders() {
		return this.headers;
	}

	public Optional<?> getBody() {
		return this.body;
	}

}
