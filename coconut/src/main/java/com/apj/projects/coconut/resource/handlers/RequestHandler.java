package com.apj.projects.coconut.resource.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.apj.projects.coconut.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.exceptions.HTTPRequestParsingException;
import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPRequestParser;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.HTTPResponseWriter;
import com.apj.projects.coconut.http.HTTPURLPath;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.http.enums.HTTPVersion;

public class RequestHandler implements Runnable {

	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private HTTPRequest request;
	private HTTPResponse response;
	private HTTPResponseWriter responseWriter;

	public RequestHandler(Socket socket, InputStream in, OutputStream out) {

		this.socket = socket;
		this.in = in;
		this.out = out;

		this.response = new HTTPResponse();
		this.responseWriter = new HTTPResponseWriter(this.out);

		response.setHTTPVersion(HTTPVersion.HTTP_1_1);

	}

	private RequestType getTypeOfRequest() {

		if (new HTTPURLPath("/rest").isParentPathOf(request.getHTTPURLPath())) {
			return RequestType.REST_RESOURCE;
		} else if (new HTTPURLPath("/file").isParentPathOf(request.getHTTPURLPath())) {
			return RequestType.FILE_RESOURCE;
		} else {
			return RequestType.UNKNOWN;
		}

	}

	private void handleRequest() {

		RequestType requestType = getTypeOfRequest();

		if (requestType == RequestType.REST_RESOURCE) {
			RESTResourceHandler.getHandler().handle(request, response);
		} else if (requestType == RequestType.FILE_RESOURCE) {

		} else {
			response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody(new File("pages/404.html")));
		}

	}

	private void closeConnection() {

		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("[ERROR]: Could not close connection");
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {

		try {
			this.request = new HTTPRequestParser(in).parse();
			handleRequest();
		} catch (BadHTTPRequestException e) {
			response.setStatus(HTTPStatusCode.BAD_REQUEST).setBody(new HTTPBody(new File("pages/400.html")));
		} catch (HTTPRequestParsingException e) {
			System.out.println(e.getMessage());
			response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR).setBody(new HTTPBody(new File("pages/500.html")));
		} finally {
			responseWriter.buildResponse(response);
			responseWriter.send();
			closeConnection();
		}

	}

}