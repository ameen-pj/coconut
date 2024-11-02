package com.apj.projects.coconut.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.apj.projects.coconut.http.enums.HTTPRequestMethod;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.body.BodyReader;
import rawhttp.core.errors.InvalidHttpRequest;

public class HTTPRequestParser {

	private static RawHttp rawHttp = new RawHttp();
	private RawHttpRequest rawRequest;

	private HTTPRequest request;
	private BufferedInputStream in;

	public HTTPRequestParser(InputStream in) {
		this.in = new BufferedInputStream(in);
	}

	public HTTPRequest parse() {
		try {
			this.rawRequest = rawHttp.parseRequest(in);
			this.request = new HTTPRequest();

			request.setHTTPVersion("HTTP/1.1");
			request.setHTTPRequestMethod(HTTPRequestMethod.valueOf(rawRequest.getMethod()));
			request.setHTTPURLPath(new HTTPURLPath(rawRequest.getUri().getPath()));
			request.setHeaders(rawRequest.getHeaders().asMap());
			request.setBody(rawRequest.eagerly().getBody().map(BodyReader::toString).orElse(null));

			return request;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidHttpRequest e) {
			System.err.println("[ERROR]: Invalid HTTP request: " + e.getMessage());
		}

		return null;
	}

}
