package com.apj.projects.coconut.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.apj.projects.coconut.utils.URLPath;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.body.BodyReader;
import rawhttp.core.errors.InvalidHttpRequest;

public class HttpRequest {

	private URLPath urlPath;
	private RawHttpRequest request;
	private static RawHttp http = new RawHttp();

	public HttpRequest(InputStream in) {

		try {
			request = http.parseRequest(new BufferedInputStream(in));
			urlPath = new URLPath(request.getUri().getPath());
		} catch (IOException | InvalidHttpRequest e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public String getMethod() {
		return request.getMethod();
	}

	public URLPath getURLPath() {
		return urlPath;
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
