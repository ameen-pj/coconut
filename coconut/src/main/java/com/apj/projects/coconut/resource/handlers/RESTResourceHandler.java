package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.URLPath;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.RESTResourceMetadata;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceMethodException;
import com.apj.projects.coconut.resource.rest.exceptions.RestResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.UnsupportedRestResourceMethodException;
import com.apj.projects.coconut.utils.AnnotationScanner;
import com.apj.projects.coconut.utils.Pair;

@Handler("rest")
public class RESTResourceHandler extends ResourceHandler {

	private static Reflections reflections = AnnotationScanner.getReflections();

	private Set<Class<?>> restResourcesClasses;
	private HashMap<String, RESTResourceMetadata> restResourcesDataMap;

	public RESTResourceHandler() throws RestResourceException {

		restResourcesClasses = scanRESTResources();
		restResourcesDataMap = new HashMap<String, RESTResourceMetadata>();

		for (Class<?> restResourceClass : restResourcesClasses) {
			RESTResourceMetadata resourceMetadata;
			try {
				resourceMetadata = new RESTResourceMetadata(restResourceClass);
				restResourcesDataMap.put(resourceMetadata.getRestResourceName(), resourceMetadata);
			} catch (RestResourceException e) {
				throw e;
			}
		}
	}

	public static Set<Class<?>> scanRESTResources() {
		return reflections
				.get(SubTypes.of(RESTResource.class).asClass().filter(withAnnotation(RESTResourceMapping.class)));
	}

	@Override
	public void handle(HTTPRequest request, HTTPResponse response) throws Exception {

		String resourceName = request.getHTTPURLPath().getPathPartByIndex(1);

		if (resourceName != null) {

			if (restResourcesDataMap.containsKey(resourceName)) {
				try {
					try {
						Pair<HTTPRequestMethod, URLPath> pair = new Pair<HTTPRequestMethod, URLPath>(
								request.getHTTPRequestMethod(), request.getHTTPURLPath());
						Method m = restResourcesDataMap.get(resourceName).getRequestMethodByPair(pair);
						Object obj = restResourcesDataMap.get(resourceName).getRestResourceObject();

						m.invoke(obj);

					} catch (UnsupportedRestResourceMethodException e) {
						response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED).setBody(new HTTPBody(e.getMessage()));
						e.printStackTrace();
					} catch (IllegalAccessException | InvocationTargetException e) {
						response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
								.setBody(new HTTPBody(e.getLocalizedMessage()));
						e.printStackTrace();
					}
				} catch (BadRESTResourceMethodException e) {
					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
							.setBody(new HTTPBody(e.getLocalizedMessage()));
				}
			} else {
				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
			}
		} else {
			response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
		}

	}

//	@Override
//	public void handle(HTTPRequest request, HTTPResponse response) {
//
//		HashMap<String, String> params = request.getHTTPURLPath().getUrlParams("/rest/{resourceName}");
//
//		if (params.containsKey("resourceName")) {
//			String resourceName = params.get("resourceName");
//
//			if (restResourcesDataMap.containsKey(resourceName)) {
//				try {
//					try {
//						Pair<HTTPRequestMethod, HTTPURLPath> pair = new Pair<HTTPRequestMethod, HTTPURLPath>(
//								request.getHTTPRequestMethod(), request.getHTTPURLPath());
//						Method m = restResourcesDataMap.get(resourceName).getRequestMethodByPair(pair);
//						Object obj = restResourcesDataMap.get(resourceName).getRestResourceObject();
//
//						m.invoke(obj);
//
//					} catch (UnsupportedRestResourceMethodException e) {
//						response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED).setBody(new HTTPBody(e.getMessage()));
//						e.printStackTrace();
//					} catch (IllegalAccessException | InvocationTargetException e) {
//						response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
//								.setBody(new HTTPBody(e.getLocalizedMessage()));
//						e.printStackTrace();
//					}
//				} catch (BadRESTResourceMethodException e) {
//					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
//							.setBody(new HTTPBody(e.getLocalizedMessage()));
//				}
//			} else {
//				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
//			}
//		}
//	}

}
