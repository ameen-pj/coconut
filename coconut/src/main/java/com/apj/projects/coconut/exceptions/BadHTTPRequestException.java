package com.apj.projects.coconut.exceptions;

public class BadHTTPRequestException extends RuntimeException {

	private static final long serialVersionUID = 6152377019198001853L;

	public BadHTTPRequestException(String msg) {
		super(msg);
	}

	public BadHTTPRequestException() {
		super("Bad HTTP Request");
	}

}
