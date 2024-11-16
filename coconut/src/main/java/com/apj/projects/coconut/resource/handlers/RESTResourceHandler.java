package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;

import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.RESTResourceMetadata;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceMethodException;
import com.apj.projects.coconut.utils.AnnotationScanner;

@Handler("rest")
public class RESTResourceHandler extends ResourceHandler {

	private static Reflections reflections = AnnotationScanner.getReflections();

	private Set<Class<?>> restResourcesClasses;
	private HashMap<String, RESTResourceMetadata> restResourcesDataMap;

	public RESTResourceHandler() {

		restResourcesClasses = scanRESTResources();
		restResourcesDataMap = new HashMap<String, RESTResourceMetadata>();

		for (Class<?> restResourceClass : restResourcesClasses) {
			RESTResourceMetadata mgr = new RESTResourceMetadata(restResourceClass);
			restResourcesDataMap.put(mgr.getRestResourceName(), mgr);
		}

	}

	public static Set<Class<?>> scanRESTResources() {
		return reflections
				.get(SubTypes.of(RESTResource.class).asClass().filter(withAnnotation(RESTResourceMapping.class)));
	}

	@Override
	public void handle(HTTPRequest request, HTTPResponse response) {
		HashMap<String, String> params = request.getHTTPURLPath().getParams("/rest/{resourceName}");

		if (params.containsKey("resourceName")) {
			String resourceName = params.get("resourceName");

			if (restResourcesDataMap.containsKey(resourceName)) {
				try {
					restResourcesDataMap.get(resourceName)
							.invokeMethodByHTTPRequestMethod(request.getHTTPRequestMethod());
					response.setStatus(HTTPStatusCode.OK);
					response.setContentType(HTTPContentTypes.TEXT_PLAIN);
				} catch (BadRESTResourceMethodException e) {
					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR)
							.setBody(new HTTPBody(e.getLocalizedMessage()));
				}
			} else {
				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
			}
		}
	}

}
