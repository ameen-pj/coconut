package com.apj.projects.coconut.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.apj.projects.coconut.exceptions.BadHTTPRequestException;

public class HTTPResponseWriter {

	private HTTPResponse response;

	private StringBuilder responseString;
	private BufferedOutputStream out;

	public HTTPResponseWriter(OutputStream out, HTTPResponse response) {

		this.out = new BufferedOutputStream(out);
		this.response = response;
		this.responseString = new StringBuilder();
	}

	public void buildResponse() throws BadHTTPRequestException {

		if (response.getHTTPVersion() != null && response.getStatus() != null) {
			responseString.append(response.getHTTPVersion() + " " + response.getStatus().getCode() + " "
					+ response.getStatus().getMsg() + "\r\n");
		} else {
			throw new BadHTTPRequestException("Invalid request line");
		}

		Map<String, List<String>> headers = response.getHeaders();
		if (headers != null && headers.size() > 0) {
			for (String key : headers.keySet()) {
				responseString.append(key + ": ");
				List<String> values = headers.get(key);
				for (int i = 0; i < values.size() - 1; i++) {
					responseString.append(values.get(i) + ",");
				}
				responseString.append(values.get(values.size() - 1));
				responseString.append("\r\n");
			}
		}

		responseString.append("\r\n");

		Optional<?> body = response.getBody();
		if (body.isPresent()) {
			responseString.append(body.get());
		}

	}

	public void send() {
		try {
			out.write(responseString.toString().getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
