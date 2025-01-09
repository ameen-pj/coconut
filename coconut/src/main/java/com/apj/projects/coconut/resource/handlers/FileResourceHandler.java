package com.apj.projects.coconut.resource.handlers;

import java.io.File;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;

@Handler("file")
public class FileResourceHandler extends ResourceHandler {

	@Override
	public void handle(HTTPRequest request, HTTPResponse response) throws Exception {

		String fileName = request.getHTTPURLPath().getPathPartByIndex(1);

		if (fileName != null) {
			File file = new File(fileName);
			HTTPBody httpBody = new HTTPBody(file);
			// Setting response
			response.setStatus(HTTPStatusCode.OK).setBody(httpBody);
		} else {
			response.setContentType(HTTPContentTypes.TEXT_PLAIN).setBody(new HTTPBody("No File Name provided"))
					.setStatus(HTTPStatusCode.NO_CONTENT);
		}
	}
}
