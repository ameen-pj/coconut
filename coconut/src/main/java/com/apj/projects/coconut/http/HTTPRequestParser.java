package com.apj.projects.coconut.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPVersion;
import com.apj.projects.coconut.http.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.http.exceptions.HTTPRequestParsingException;

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

	public HTTPRequest parse() throws BadHTTPRequestException, HTTPRequestParsingException {

		try {
			this.rawRequest = rawHttp.parseRequest(in);
			this.request = new HTTPRequest();
			request.setHTTPVersion(HTTPVersion.HTTP_1_1);
			request.setHTTPRequestMethod(HTTPRequestMethod.valueOf(rawRequest.getMethod()));
			request.setQueryParamString(rawRequest.getUri().getQuery());
			request.setHTTPURLPath(new URLPath(rawRequest.getUri().getPath()));
			request.setHeaders(rawRequest.getHeaders().asMap());
			request.setBody(rawRequest.eagerly().getBody().map(BodyReader::toString).orElse(null));
			return request;

		} catch (IOException e) {
			throw new HTTPRequestParsingException(e.getMessage());
		} catch (InvalidHttpRequest e) {
			throw new BadHTTPRequestException(e.getMessage());
		}
	}

}
