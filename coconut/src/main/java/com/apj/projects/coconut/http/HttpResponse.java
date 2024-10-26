package com.apj.projects.coconut.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.apj.projects.coconut.utils.HTTPContentTypes;
import com.apj.projects.coconut.utils.HTTPStatusCodes;

public class HttpResponse {

	private OutputStream out;

	private StringBuilder response;
	private StringBuilder headers;
	private String requestLine;
	private String body;

	public HttpResponse(OutputStream out) {
		this.out = out;

		response = new StringBuilder();
		headers = new StringBuilder();
	}

	public void setStatus(HTTPStatusCodes statusCode) {
		requestLine = "HTTP/1.1 " + statusCode.getStatusCode() + " " + statusCode.getMsg() + "\r\n";
	}

	public void setContentType(HTTPContentTypes contentType) {
		setHeader("Content-Type", contentType.getName());
	}

	public <T> void setHeaders(HashMap<String, T> map) {

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

	public void sendFile(String fileName) {

		response.append(requestLine);
		response.append(headers.toString());
		response.append("\r\n");

		try {
			out.write(response.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			File file = new File(fileName);
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

			byte[] buffer = new byte[512];
			int count;
			try {
				while ((count = fis.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}
				fis.close();
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[ERROR]1: Something went wrong");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR]: Could not find file");
		}

	}

	public void send() {

		response.append(requestLine);
		response.append(headers.toString());
		response.append("\r\n");

		if (body != null) {
			response.append(body);
		}

		try {
			out.write(response.toString().getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
