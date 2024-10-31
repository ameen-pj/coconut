package com.apj.projects.coconut.http;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.apj.projects.coconut.utils.FileSender;

public class HttpResponse {

	private BufferedOutputStream out;

	private StringBuilder response;
	private StringBuilder headers;
	private String requestLine;
	private String body;

	public HttpResponse(OutputStream out) {
		this.out = new BufferedOutputStream(out);

		response = new StringBuilder();
		headers = new StringBuilder();
	}

	public void setStatus(HTTPStatusCodes statusCode) {
		requestLine = "HTTP/1.1 " + statusCode.getStatusCode() + " " + statusCode.getMsg() + "\r\n";
	}

	public void setContentType(HTTPContentTypes contentType) {
		setHeader("Content-Type", contentType.getName());
	}

	public void setHeaders(HashMap<String, ?> map) {

		for (String key : map.keySet()) {
			headers.append(key + ": " + map.get(key) + "\r\n");
		}
	}

	public <T> void setHeader(String key, T value) {
		headers.append(key + ": " + value + "\r\n");
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void send(HTTPStatusCodes statusCode, HashMap<String, ?> headers, String body) {

		setStatus(statusCode);
		setHeaders(headers);
		setBody(body);

		response.append(requestLine);
		response.append(headers.toString());
		response.append("\r\n");

		if (this.body != null) {
			response.append(this.body);
		}

		try {
			out.write(response.toString().getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendFile(String fileName, HTTPStatusCodes statusCode) {

		FileSender fs = new FileSender(fileName);

		try {
			fs.ready();

			setStatus(statusCode);
			setHeader("Content-Length", fs.getFileSize());

			for (HTTPContentTypes t : HTTPContentTypes.values()) {
				if (fileName.contains(t.getExtension())) {
					setContentType(t);
					break;
				}
			}

			response.append(requestLine);
			response.append(headers.toString());
			response.append("\r\n");

			out.write(response.toString().getBytes());

			fs.copyTo(out);

			out.flush();

		} catch (FileNotFoundException e) {
			sendFile("pages/404.html", HTTPStatusCodes.NOT_FOUND);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
