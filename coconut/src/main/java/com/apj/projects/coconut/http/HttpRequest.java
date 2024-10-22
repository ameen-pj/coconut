package com.apj.projects.coconut.http;


public class HttpRequest {
	
	private String method;
	private String URL;
	private String httpVersion;
	
	
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
	
	
	public String getMethod() {
		return method;
	}
	
	public String getURL() {
		return URL;
	}
	
	public String getHttpVersion() {
		return httpVersion;
	}
	
	
}
