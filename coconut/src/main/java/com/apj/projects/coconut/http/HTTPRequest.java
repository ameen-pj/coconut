package com.apj.projects.coconut.http;

import java.util.HashMap;
import java.util.Optional;

import com.apj.projects.coconut.exceptions.HTTPHeaderFieldNotFoundException;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;

public class HTTPRequest {

	private String HTTPVersion;
	private HTTPRequestMethod HTTPRequestMethod;
	private HTTPURLPath HTTPURLPath;
	private HashMap<String, String> headers;
	private Optional<?> body;

	// Setters

	public void setHTTPVersion(String version) {
		this.HTTPVersion = version;
	}

	public void setHTTPRequestMethod(HTTPRequestMethod method) {
		this.HTTPRequestMethod = method;
	}

	public void setHTTPURLPath(HTTPURLPath path) {
		this.HTTPURLPath = path;
	}

	public void setHeaders(HashMap<String, String> headers) {
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

	public HashMap<String, ?> getHeaders() {
		return this.headers;
	}

	public String getHeaderByField(String field) throws HTTPHeaderFieldNotFoundException {

		String val = (String) headers.get(field);
		if (val == null) {
			throw new HTTPHeaderFieldNotFoundException();
		}
		return val;

	}

	public Optional<?> getBody() {
		return body;
	}

}
