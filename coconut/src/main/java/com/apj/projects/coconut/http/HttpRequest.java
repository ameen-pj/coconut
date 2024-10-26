package com.apj.projects.coconut.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.body.BodyReader;
import rawhttp.core.errors.InvalidHttpRequest;

public class HttpRequest {

	private RawHttpRequest request;
	private static RawHttp http = new RawHttp();

	public HttpRequest(InputStream in) {

		try {
			request = http.parseRequest(in);
		} catch (IOException | InvalidHttpRequest e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public String getMethod() {
		return request.getMethod();
	}

	public URI getURI() {
		return request.getUri();
	}

	public String getBody() {

		try {
			return request.eagerly().getBody().map(BodyReader::toString).orElse(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String toString() {

		return "[" + request.getMethod() + "]" + " :: " + request.getUri();

	}

}
