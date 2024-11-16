package com.apj.projects.coconut.resource.rest.exceptions;

public class BadRESTResourceMethodException extends RuntimeException {

	private static final long serialVersionUID = 6277042328138186664L;

	public BadRESTResourceMethodException() {
		super();
	}

	public BadRESTResourceMethodException(String msg) {
		super(msg);
	}

}
