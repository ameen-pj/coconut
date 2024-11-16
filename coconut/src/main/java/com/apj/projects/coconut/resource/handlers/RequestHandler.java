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

import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPRequestParser;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.HTTPResponseWriter;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.http.enums.HTTPVersion;
import com.apj.projects.coconut.http.exceptions.BadHTTPBodyException;
import com.apj.projects.coconut.http.exceptions.BadHTTPHeadersException;
import com.apj.projects.coconut.http.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.http.exceptions.BadHTTPResponseException;
import com.apj.projects.coconut.http.exceptions.BadURLException;
import com.apj.projects.coconut.http.exceptions.HTTPRequestParsingException;
import com.apj.projects.coconut.http.exceptions.HTTPResponseWriterException;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.handlers.exceptions.GeneralServerException;
import com.apj.projects.coconut.resource.handlers.exceptions.ResourceHandlerNotImplementedException;
import com.apj.projects.coconut.utils.AnnotationScanner;
import com.apj.projects.coconut.utils.Pair;

public class RequestHandler implements Runnable {

	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private HTTPRequest request;
	private HTTPResponse response;
	private HTTPResponseWriter responseWriter;

	private static HashMap<String, Pair<Object, Method>> handlers = getHandlers();

	public RequestHandler(Socket socket, InputStream in, OutputStream out) {
		// IO
		this.socket = socket;
		this.in = in;
		this.out = out;
		// Request Response
		this.response = new HTTPResponse();
		this.responseWriter = new HTTPResponseWriter(this.out);

		response.setHTTPVersion(HTTPVersion.HTTP_1_1);

	}

//	private ResourceType getResourceType() {
//
//		if (new HTTPURLPath("/rest").isParentPathOf(request.getHTTPURLPath())) {
//			return ResourceType.REST_RESOURCE;
//		} else if (new HTTPURLPath("/file").isParentPathOf(request.getHTTPURLPath())) {
//			return ResourceType.FILE_RESOURCE;
//		} else {
//			return ResourceType.UNKNOWN;
//		}
//
//	}

//	private void handleRequest() {
//
//		ResourceType requestType = getResourceType();
//
//		if (requestType == ResourceType.REST_RESOURCE) {
//			RESTResourceHandler.getHandler().handle(request, response);
//		} else if (requestType == ResourceType.FILE_RESOURCE) {
//
//		} else {
//			response.setStatus(HTTPStatusCode.NOT_FOUND);
//		}
//	}

	private void handleRequest()
			throws BadURLException, GeneralServerException, ResourceHandlerNotImplementedException {
		String resourceType = request.getHTTPURLPath().getResourceType();
		Pair<Object, Method> resourceHandlerInfo = handlers.get(resourceType);

		if (resourceHandlerInfo != null) {
			Method invokeMethod = resourceHandlerInfo.getValue();
			Object handlerObject = resourceHandlerInfo.getKey();
			try {
				invokeMethod.invoke(handlerObject, request, response);
			} catch (IllegalAccessException e) {
				throw new GeneralServerException(e.getMessage());
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				System.err.println("[ERROR]: Could not close connection");
				e.printStackTrace();
			}
		}
	}

	private static HashMap<String, Pair<Object, Method>> getHandlers() {

		HashMap<String, Pair<Object, Method>> resourceHandler = new HashMap<>();

		Reflections reflections = AnnotationScanner.getReflections();
		// Get all handler classes (subtypes of ResourceHandler and annotated with
		// Handler)
		Set<Class<?>> handlerClasses = reflections.get(SubTypes.of(ResourceHandler.class).asClass()
				.filter(ReflectionUtilsPredicates.withAnnotation(Handler.class)));

		for (Class<?> handlerClass : handlerClasses) {
			String resourceType = handlerClass.getAnnotation(Handler.class).value();

			try {
				Method handleMethod = handlerClass.getDeclaredMethod("handle", HTTPRequest.class, HTTPResponse.class);
				Object object = handlerClass.getDeclaredConstructor().newInstance();

				try {
					Method setResourceType = handlerClass.getDeclaredMethod("setResourceType", String.class);
					setResourceType.invoke(object, resourceType);
					resourceHandler.put(resourceType, new Pair<Object, Method>(object, handleMethod));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					System.out.println("[ERROR]: Could not set resource type");
					System.err.println(e.getMessage());
				}

			} catch (NoSuchMethodException e) {
				System.err.println("[ERROR]: Could not load " + resourceType
						+ " handler as handle method is not declared or constructor is not declared");
				System.err.println(e.getMessage());
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				System.err.println("[ERROR]: Could not create " + resourceType + "handler object");
			}

		}
		return resourceHandler;

	}

	@Override
	public void run() {

		try {
			this.request = new HTTPRequestParser(in).parse();
			handleRequest();
		} catch (BadHTTPRequestException | BadURLException e) {
			System.err.println(e.getMessage());
			response.setStatus(HTTPStatusCode.BAD_REQUEST);
		} catch (HTTPRequestParsingException | GeneralServerException e) {
			System.err.println(e.getMessage());
			response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR);
		} catch (ResourceHandlerNotImplementedException e) {
			response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED);
			System.err.println(e.getMessage());
		} finally {
			try {
				responseWriter.buildResponse(response);
			} catch (BadHTTPResponseException e) {
				// dereference the old response object and initialize new error response object
				response = new HTTPResponse();
				response.setStatus(HTTPStatusCode.NO_CONTENT);
				System.err.println(e.getMessage());
			} catch (BadHTTPHeadersException e) {
				System.err.println(e.getMessage());
				response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					responseWriter.send();
				} catch (BadHTTPBodyException e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				} catch (HTTPResponseWriterException e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
				closeConnection();
			}
		}
	}
}