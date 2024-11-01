package com.apj.projects.coconut.http;

import java.io.InputStream;

public class HTTPRequestParser {

	private HTTPRequest request;
	private InputStream in;

	public HTTPRequestParser(InputStream in) {
		this.request = new HTTPRequest();
	}

	public HTTPRequest parse() {

		parseRequestLine();
		parseHeaders();
		parseBody();

		return request;

	}

	private void parseRequestLine() {
		// TODO Auto-generated method stub

	}

	private void parseHeaders() {
		// TODO Auto-generated method stub

	}

	private void parseBody() {
		// TODO Auto-generated method stub

	}

}
