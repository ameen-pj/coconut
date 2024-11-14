package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import com.apj.projects.coconut.exceptions.BadRESTResourceMethodException;
import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.RESTResourceManager;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

public class RESTResourceHandler {

	private static RESTResourceHandler restHandler;
	private static Reflections reflections = new Reflections("com.apj.projects.coconut");

	private Set<Class<?>> restResourcesClasses;
	private HashMap<String, RESTResourceManager> restResourcesDataMap;

	private RESTResourceHandler() {

		restResourcesClasses = scanRESTResources();
		restResourcesDataMap = new HashMap<String, RESTResourceManager>();

		for (Class<?> restResourceClass : restResourcesClasses) {
			RESTResourceManager mgr = new RESTResourceManager(restResourceClass);
			restResourcesDataMap.put(mgr.getRestResourceName(), mgr);
		}

	}

	public HTTPResponse handle(HTTPRequest request, HTTPResponse response) {

		HashMap<String, String> params = request.getHTTPURLPath().getParams("/rest/{resourceName}");
		if (params.containsKey("resourceName")) {

			String resourceName = params.get("resourceName");

			if (restResourcesDataMap.containsKey(resourceName)) {
				try {
					restResourcesDataMap.get(resourceName)
							.invokeMethodByHTTPRequestMethod(request.getHTTPRequestMethod());
					response.setStatus(HTTPStatusCode.OK);
				} catch (BadRESTResourceMethodException e) {
					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
							.setBody(new HTTPBody(e.getLocalizedMessage()));
				}

			} else {
				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody(new File("pages/404.html")));
			}
		}

		return response;

	}

	static RESTResourceHandler getHandler() {
		if (restHandler == null) {
			restHandler = new RESTResourceHandler();
		}
		return restHandler;
	}

	public static Set<Class<?>> scanRESTResources() {
		return reflections
				.get(SubTypes.of(RESTResource.class).asClass().filter(withAnnotation(RESTResourceMapping.class)));
	}

	public static void main(String[] args) {
		RESTResourceHandler.getHandler();
	}

}
