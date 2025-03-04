package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.SubTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ReflectionUtilsPredicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPRequestParser;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.HTTPResponseWriter;
import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.http.enums.HTTPVersion;
import com.apj.projects.coconut.http.exceptions.BadHTTPBodyException;
import com.apj.projects.coconut.http.exceptions.BadHTTPHeadersException;
import com.apj.projects.coconut.http.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.http.exceptions.BadHTTPResponseException;
import com.apj.projects.coconut.http.exceptions.HTTPRequestParsingException;
import com.apj.projects.coconut.http.exceptions.HTTPResponseWriterException;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.handlers.exceptions.GeneralServerException;
import com.apj.projects.coconut.resource.handlers.exceptions.ResourceHandlerNotImplementedException;
import com.apj.projects.coconut.utils.AnnotationScanner;
import com.apj.projects.coconut.utils.Pair;

public class RequestManager implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(RequestManager.class);

	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private HTTPRequest request;
	private HTTPResponse response;
	private HTTPResponseWriter responseWriter;

	private static HashMap<String, Pair<Object, Method>> handlers = getHandlers();

	public RequestManager(Socket socket, InputStream in, OutputStream out) {
		// IO
		this.socket = socket;
		this.in = in;
		this.out = out;
		// Request Response
		this.response = new HTTPResponse();
		this.responseWriter = new HTTPResponseWriter(this.out);

		response.setHTTPVersion(HTTPVersion.HTTP_1_1);

	}

	private void handleRequest() throws GeneralServerException, ResourceHandlerNotImplementedException {

		String resourceType = request.getHTTPURLPath().getPathPartByIndex(0);
		Pair<Object, Method> resourceHandlerInfo = handlers.get(resourceType);

		if (resourceHandlerInfo != null) {
			Method invokeMethod = resourceHandlerInfo.getValue();
			Object handlerObject = resourceHandlerInfo.getKey();
			try {
				invokeMethod.invoke(handlerObject, request, response);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new GeneralServerException(e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new GeneralServerException(e.getMessage());
			}

		} else {
			throw new ResourceHandlerNotImplementedException(resourceType + " handler is not yet implemented");
		}

	}

	private void closeConnection() {

		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("Could not close connection", e);
			}
		}
	}

	private static HashMap<String, Pair<Object, Method>> getHandlers() {

		HashMap<String, Pair<Object, Method>> resourceHandler = new HashMap<>();

		Reflections reflections = AnnotationScanner.getCoconutReflections();
		// Get all handler classes (subtypes of ResourceHandler and annotated with
		// Handler)
		Set<Class<?>> handlerClasses = reflections.get(SubTypes.of(ResourceHandler.class).asClass()
				.filter(ReflectionUtilsPredicates.withAnnotation(Handler.class)));

		for (Class<?> handlerClass : handlerClasses) {
			String resourceType = handlerClass.getAnnotation(Handler.class).value();

			try {
				Method handleMethod = handlerClass.getDeclaredMethod("handle", HTTPRequest.class, HTTPResponse.class);
				Object object = handlerClass.getDeclaredConstructor().newInstance();

				resourceHandler.put(resourceType, new Pair<Object, Method>(object, handleMethod));

			} catch (NoSuchMethodException e) {
				logger.error("Could not load " + resourceType
						+ " handler as handle method is not declared or constructor is not declared", e);
			} catch (SecurityException e) {
				logger.error("Internal Error", e);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				logger.error("Could not create " + resourceType + "handler object", e);
			}

		}
		return resourceHandler;

	}

	@Override
	public void run() {

		try {
			this.request = new HTTPRequestParser(in).parse();
			response.setContentType(HTTPContentTypes.TEXT_PLAIN);
			handleRequest();
		} catch (BadHTTPRequestException e) {
			logger.error("BadHTTPRequestException", e);
			response.setStatus(HTTPStatusCode.BAD_REQUEST).setBody(new HTTPBody(e.getMessage()));
		} catch (HTTPRequestParsingException | GeneralServerException e) {
			logger.error("Error", e);
			response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR).setBody(new HTTPBody(e.getMessage()));
		} catch (ResourceHandlerNotImplementedException e) {
			response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED).setBody(new HTTPBody(e.getMessage()));
			logger.error("ResourceHandlerNotImplementedException", e);
		} finally {
			try {
				responseWriter.buildResponse(response);
				logger.info("[" + request.getHTTPRequestMethod() + "] " + request.getHTTPURLPath() + " ~ "
						+ response.getStatus().getCode() + " " + response.getStatus().getMsg());
			} catch (BadHTTPResponseException e) {
				// dereference the old response object and initialize new error response object
				response = new HTTPResponse();
				response.setStatus(HTTPStatusCode.NO_CONTENT).setBody(new HTTPBody(e.getMessage()));
				logger.error("BadHTTPResponseException", e);
			} catch (BadHTTPHeadersException e) {
				logger.error("BadHTTPHeadersException", e);
				response = new HTTPResponse();
				response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR).setBody(new HTTPBody(e.getMessage()));
			} finally {
				try {
					responseWriter.send();
				} catch (BadHTTPBodyException e) {
					logger.error("BadHTTPBodyException", e);

				} catch (HTTPResponseWriterException e) {
					logger.error("HTTPResponseWriterException", e);
				}
				closeConnection();
			}
		}
	}
}