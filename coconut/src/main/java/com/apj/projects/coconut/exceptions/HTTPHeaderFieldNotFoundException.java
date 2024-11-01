package com.apj.projects.coconut.exceptions;

public class HTTPHeaderFieldNotFoundException extends Exception {

	private static final long serialVersionUID = 2839643963229370577L;

	public HTTPHeaderFieldNotFoundException() {
		super("The passed header field could not be found");
	}

	public HTTPHeaderFieldNotFoundException(String msg) {
		super(msg);
	}

}
