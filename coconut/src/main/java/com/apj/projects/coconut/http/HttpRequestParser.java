package com.apj.projects.coconut.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.apj.projects.coconut.utils.SpecialCharacters;

public class HttpRequestParser {
	
	private BufferedReader reader;
	private HttpRequest request;
	
	public HttpRequestParser(InputStream in) {
		
		this.reader = new BufferedReader(new InputStreamReader(in));
		this.request = new HttpRequest();
		parseRequestLine(request);
		parseHeaderFields(request);
	}
	
	public HttpRequest build() {
		return request;
	}
	
	
	private void parseHeaderFields(HttpRequest request) {
		
		HashMap<String, String> headerFields = new HashMap<String, String>();
		
		int _byte;
		StringBuilder line = new StringBuilder();
		try {
			while ((_byte = reader.read()) != -1) {				
				if ((_byte) == SpecialCharacters.CR && (reader.read() == SpecialCharacters.NL)) {
					// if empty line.. headers ended
					if (line.toString().length() == 0) {
						request.setHeaderFields(headerFields);
						break;
					}
					else {
						String[] headerLine = line.toString().split(": ");
						headerFields.put(headerLine[0], headerLine[1]);
						line.delete(0, line.length());
					}
				} else {
					line.append((char)_byte);
				}				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void parseRequestLine(HttpRequest request) {
				
		int _byte;
		StringBuilder line = new StringBuilder();
		
		try { 
			
			String methodType = "";
			String URL = "";
			String HTTPVersion = "";
			
			boolean parsedMethodType  = false;
			boolean parsedURL 		  = false;
			boolean parsedHTTPVersion = false;
			
			while ((_byte = reader.read()) != -1) {
				if (_byte == SpecialCharacters.SP) {
					
					if (!parsedMethodType) {
						methodType = line.toString();
						line.delete(0, line.length());
						parsedMethodType = true;
					}
					else if (!parsedURL) {
						URL = line.toString();
						line.delete(0, line.length());
						parsedURL = true;
					}				
				}
				
				if (_byte == SpecialCharacters.CR) {
					if ((_byte = reader.read()) == SpecialCharacters.NL) {
						if (!parsedHTTPVersion) {
							HTTPVersion = line.toString();
							line.delete(0, line.length());
							parsedHTTPVersion = true;
						}	
						break;
					}
				}
				
				line.append((char)_byte);
			}
			
			request.setMethod(methodType.trim());
			request.setURL(URL.trim());
			request.setHttpVersion(HTTPVersion.trim());
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
