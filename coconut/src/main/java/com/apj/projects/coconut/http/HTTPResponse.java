package com.apj.projects.coconut.http;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.http.enums.HTTPVersion;

public class HTTPResponse {

	private String HTTPVersion;
	private HTTPStatusCode httpStatusCode;
	private HashMap<String, List<String>> headers = new HashMap<>();
	private HTTPBody body;

	// Setters

	public HTTPResponse setHTTPVersion(HTTPVersion version) {
		this.HTTPVersion = version.getVersion();
		return this;
	}

	public HTTPResponse setStatus(HTTPStatusCode statusCode) {
		this.httpStatusCode = statusCode;
		return this;
	}

	public HTTPResponse setContentType(HTTPContentTypes contentType) {
		this.headers.put("Content-Type", Arrays.asList(contentType.getName()));
		return this;
	}

	public HTTPResponse setContentLength(long contentLength) {
		this.headers.put("Content-Length", Arrays.asList(Long.toString(contentLength)));
		return this;
	}

	public HTTPResponse setHeaders(HashMap<String, List<String>> headers) {
		this.headers = headers;
		return this;
	}

	public HTTPResponse setHeader(String key, String value) {
		headers.put(key, Arrays.asList(value));
		return this;
	}

	public HTTPResponse setBody(HTTPBody httpBody) {
		this.body = httpBody;

		if (httpBody.isMediaType()) {
			for (HTTPContentTypes t : HTTPContentTypes.values()) {
				if (((File) httpBody.getContent().get()).getName().contains(t.getExtension())) {
					setContentType(t);
					break;
				}
			}
		}
		return this;
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

	public HTTPBody getBody() {
		return this.body;
	}

}
