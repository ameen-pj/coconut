package com.apj.projects.coconut.http.enums;

public enum HTTPVersion {

	HTTP_1_1("HTTP/1.1"), HTTP_1("HTTP/1.0"), HTTP_2("HTTP/2.0");

	private String version;

	HTTPVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

}
