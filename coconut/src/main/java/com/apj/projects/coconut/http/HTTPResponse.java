package com.apj.projects.coconut.http;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;

public class HTTPResponse {

	private String HTTPVersion;
	private HTTPStatusCode httpStatusCode;
	private HashMap<String, List<String>> headers = new HashMap<>();
	private HTTPBody body;

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

	public void setHeader(String key, String value) {
		headers.put(key, Arrays.asList(value));
	}

	public void setBody(HTTPBody httpBody) {
		this.body = httpBody;

		if (httpBody.isMediaType()) {
			for (HTTPContentTypes t : HTTPContentTypes.values()) {
				if (((File) httpBody.getContent().get()).getName().contains(t.getExtension())) {
					setContentType(t);
					break;
				}
			}
		} else {
			setContentType(HTTPContentTypes.TEXT_PLAIN);
		}

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
