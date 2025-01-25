package com.apj.projects.coconut.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPVersion;
import com.apj.projects.coconut.http.exceptions.BadHTTPHeadersException;

public class HTTPRequest {

	private String HTTPVersion;
	private HTTPRequestMethod requestMethod;
	private URLPath HTTPURLPath;
	private String queryParamString;
	private Map<String, List<String>> headers;
	private Optional<?> body;

	// Setters

	public void setHTTPVersion(HTTPVersion version) {
		this.HTTPVersion = version.getVersion();
	}

	public void setHTTPRequestMethod(HTTPRequestMethod method) {
		this.requestMethod = method;
	}

	public void setHTTPURLPath(URLPath path) {
		this.HTTPURLPath = path;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public <T> void setBody(T data) {
		this.body = Optional.ofNullable(data);
	}

	public void setQueryParamString(String queryParamString) {
		this.queryParamString = queryParamString;
	}

	// Getters

	public String getHTTPVersion() {
		return this.HTTPVersion;
	}

	public HTTPRequestMethod getHTTPRequestMethod() {
		return this.requestMethod;
	}

	public URLPath getHTTPURLPath() {
		return this.HTTPURLPath;
	}

	public Map<String, ?> getHeaders() {
		return this.headers;
	}

	public List<String> getHeaderByField(String field) throws BadHTTPHeadersException {

		List<String> val = headers.get(field);
		if (val == null) {
			throw new BadHTTPHeadersException("Could not get request header field : " + field);
		}
		return val;

	}

	public Optional<?> getBody() {
		return body;
	}

	public String getQueryParamString() {
		return queryParamString;
	}

}
