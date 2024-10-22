package com.apj.projects.coconut.http;

import java.util.HashMap;

public class HttpRequest {
	
	private String method;
	private String URL;
	private String httpVersion;
	private HashMap<String,String> headerFields;
	
	public HttpRequest() {
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setURL(String URL) {
		this.URL = URL;
	}
	
	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}
	
	public void setHeaderFields(HashMap<String, String> headerFields) {
		this.headerFields = headerFields;
	}
	
	
	public String getMethod() {
		return method;
	}
	
	public String getURL() {
		return URL;
	}
	
	public String getHttpVersion() {
		return httpVersion;
	}
	
	public HashMap<String,String> getHeaderFields() {
		return headerFields;
	}
	
	
}
