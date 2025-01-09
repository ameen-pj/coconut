package com.apj.projects.coconut.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.apj.projects.coconut.http.exceptions.BadHTTPBodyException;
import com.apj.projects.coconut.http.exceptions.BadHTTPHeadersException;
import com.apj.projects.coconut.http.exceptions.BadHTTPResponseException;
import com.apj.projects.coconut.http.exceptions.HTTPResponseWriterException;
import com.apj.projects.coconut.utils.FileSender;

public class HTTPResponseWriter {

	private HTTPResponse response;

	private StringBuilder responseString;
	private BufferedOutputStream out;

	public HTTPResponseWriter(OutputStream out) {
		this.out = new BufferedOutputStream(out);
		this.responseString = new StringBuilder();
	}

	public void buildResponse(HTTPResponse response) throws BadHTTPResponseException, BadHTTPHeadersException {

		this.response = response;

		if (response.getHTTPVersion() != null && response.getStatus() != null) {
			responseString.append(response.getHTTPVersion() + " " + response.getStatus().getCode() + " "
					+ response.getStatus().getMsg() + "\r\n");
		} else {
			throw new BadHTTPResponseException(
					"Cannot build a response with an invalid HTTP version or HTTP status code");
		}

		Map<String, List<String>> headers = this.response.getHeaders();
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
		} else {
			throw new BadHTTPHeadersException("Cannot build a response with empty/null headers");
		}

		responseString.append("\r\n");
	}

	public void send() throws BadHTTPBodyException, HTTPResponseWriterException {
		try {
			out.write(responseString.toString().getBytes()); // Headers
			HTTPBody body = response.getBody();

			if (body != null) {
				Optional<?> bodyContent = body.getContent();

				if (!response.getBody().isMediaType() && bodyContent.isPresent()) {
					out.write(bodyContent.get().toString().getBytes());
				}

				if (bodyContent.isPresent() && response.getBody().isMediaType()) {
					File file = (File) bodyContent.get();
					try {
						FileSender sender = new FileSender(file);
						sender.ready();
						sender.copyTo(out);
					} catch (FileNotFoundException e) {
						throw new BadHTTPBodyException("Cannot send the file " + file.getAbsolutePath()
								+ "as the following resource could not be found");
					} catch (IOException e) {
						throw new HTTPResponseWriterException(
								"Could not write file to outputstream: " + e.getMessage());
					}
				}
			}
			out.flush();
		} catch (IOException e) {
			throw new HTTPResponseWriterException("Could not flush response to buffered stream: " + e.getMessage());
		}
	}
}
