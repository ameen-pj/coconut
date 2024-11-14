package com.apj.projects.coconut.exceptions;

public class HTTPRequestParsingException extends RuntimeException {

	private static final long serialVersionUID = 1525958747380778350L;

	public HTTPRequestParsingException() {
		super("Could not parse the HTTPRequest");
	}

	public HTTPRequestParsingException(String msg) {
		super(msg);
	}

}
