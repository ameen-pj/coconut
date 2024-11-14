package com.apj.projects.coconut.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.apj.projects.coconut.exceptions.HTTPHeaderFieldNotFoundException;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPVersion;

public class HTTPRequest {

	private String HTTPVersion;
	private HTTPRequestMethod HTTPRequestMethod;
	private HTTPURLPath HTTPURLPath;
	private Map<String, List<String>> headers;
	private Optional<?> body;

	// Setters

	public void setHTTPVersion(HTTPVersion version) {
		this.HTTPVersion = version.getVersion();
	}

	public void setHTTPRequestMethod(HTTPRequestMethod method) {
		this.HTTPRequestMethod = method;
	}

	public void setHTTPURLPath(HTTPURLPath path) {
		this.HTTPURLPath = path;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public <T> void setBody(T data) {
		this.body = Optional.ofNullable(data);
	}

	// Getters

	public String getHTTPVersion() {
		return this.HTTPVersion;
	}

	public HTTPRequestMethod getHTTPRequestMethod() {
		return this.HTTPRequestMethod;
	}

	public HTTPURLPath getHTTPURLPath() {
		return this.HTTPURLPath;
	}

	public Map<String, ?> getHeaders() {
		return this.headers;
	}

	public List<String> getHeaderByField(String field) throws HTTPHeaderFieldNotFoundException {

		List<String> val = headers.get(field);
		if (val == null) {
			throw new HTTPHeaderFieldNotFoundException();
		}
		return val;

	}

	public Optional<?> getBody() {
		return body;
	}

}
