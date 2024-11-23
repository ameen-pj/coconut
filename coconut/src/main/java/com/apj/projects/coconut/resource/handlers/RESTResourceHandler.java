package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.URLPath;
import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.http.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.json.JSONObjectMapper;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.handlers.exceptions.GeneralServerException;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.RESTResourceMetadata;
import com.apj.projects.coconut.resource.rest.annotations.Consumes;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceMethodException;
import com.apj.projects.coconut.resource.rest.exceptions.RestResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.UnsupportedRestResourceMethodException;
import com.apj.projects.coconut.utils.AnnotationScanner;
import com.apj.projects.coconut.utils.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
			resourceMetadata = new RESTResourceMetadata(restResourceClass);
			restResourcesDataMap.put(resourceMetadata.getRestResourceName(), resourceMetadata);
		}
	}

	public static Set<Class<?>> scanRESTResources() {
		return reflections
				.get(SubTypes.of(RESTResource.class).asClass().filter(withAnnotation(RESTResourceMapping.class)));
	}

	public void resourceMethodHandler(String resourceName, HTTPRequest request, HTTPResponse response)
			throws UnsupportedRestResourceMethodException, GeneralServerException, BadHTTPRequestException,
			BadRESTResourceMethodException {

		Pair<HTTPRequestMethod, URLPath> requestInfoPair = new Pair<HTTPRequestMethod, URLPath>(
				request.getHTTPRequestMethod(), request.getHTTPURLPath());

		Method resourceMethod = restResourcesDataMap.get(resourceName).getRequestMethodByPair(requestInfoPair);
		Object resourceObject = restResourcesDataMap.get(resourceName).getRestResourceObject();

		try {

			// Getting method annotations
			Annotation consumesAnnotation = resourceMethod.getDeclaredAnnotation(Consumes.class);
			Annotation producesAnnotation = resourceMethod.getDeclaredAnnotation(Produces.class);

			Parameter[] resourceMethodParams = resourceMethod.getParameters();
			ArrayList<Object> methodArgs = new ArrayList<Object>();

			// URL Query Params
			String queryParamString = request.getQueryParamString();
			HashMap<String, Object> requestQueryParams = new HashMap<String, Object>();
			if (queryParamString != null) {
				requestQueryParams = URLPath.getQueryParams(queryParamString);
			}

			for (Parameter resourceMethodParam : resourceMethodParams) {

				Annotation[] paramAnnotations = resourceMethodParam.getDeclaredAnnotations();

				for (Annotation paramAnnotation : paramAnnotations) {

					if (paramAnnotation instanceof QueryParams) {

						String query = ((QueryParams) paramAnnotation).value();
						Object value = requestQueryParams.get(query);

						if (value == null) {
							throw new BadHTTPRequestException("Query param value of " + query + " is null");
						} else {
							methodArgs.add(value);
						}

					} else if (paramAnnotation instanceof RequestBody) {
						Optional<?> body = request.getBody();
						if (consumesAnnotation != null) {
							HTTPContentTypes consumesContentType = ((Consumes) consumesAnnotation).value();
							// Processing JSON body
							if (consumesContentType == HTTPContentTypes.APPLICATION_JSON) {
								String jsonString = "";
								if (body.isPresent()) {
									jsonString = request.getBody().get().toString();
								}
								try {
									Object obj = JSONObjectMapper.getMapper().readValue(jsonString,
											resourceMethodParam.getType());
									methodArgs.add(obj);
								} catch (JsonMappingException e) {
									e.printStackTrace();
									throw new GeneralServerException(e.getMessage());
								} catch (JsonProcessingException e) {
									e.printStackTrace();
									throw new GeneralServerException(e.getMessage());
								}
							} else {
								throw new GeneralServerException(
										consumesContentType + " not implemented for this handler");
							}
						} else {
							throw new BadRESTResourceMethodException("No @consumes annotation present");
						}
					}
				}
			}
			// Invoking the method and returning the value
			Optional<?> returnValue = Optional.ofNullable(resourceMethod.invoke(resourceObject, methodArgs.toArray()));

			if (returnValue.isPresent()) {
				if (producesAnnotation != null) {
					HTTPContentTypes producesContentType = ((Produces) producesAnnotation).value();
					// Return JSON Response
					if (producesContentType == HTTPContentTypes.APPLICATION_JSON) {
						String json = JSONObjectMapper.getMapper().writeValueAsString(returnValue.get());
						response.setStatus(HTTPStatusCode.OK).setBody(new HTTPBody(json))
								.setContentType(HTTPContentTypes.APPLICATION_JSON);
					} else {
						throw new GeneralServerException(producesContentType + " not implemented by this handler");
					}
				} else {
					throw new BadRESTResourceMethodException("No @produces annotation present");
				}

			} else {
				response.setStatus(HTTPStatusCode.OK);
			}
		} catch (IllegalAccessException | InvocationTargetException | JsonProcessingException e) {
			e.printStackTrace();
			throw new GeneralServerException(e.getMessage());
		}

	}

	@Override
	public void handle(HTTPRequest request, HTTPResponse response) throws Exception {

		String resourceName = request.getHTTPURLPath().getPathPartByIndex(1);

		if (resourceName != null) {
			if (restResourcesDataMap.containsKey(resourceName)) {
				try {
					resourceMethodHandler(resourceName, request, response);
				} catch (UnsupportedRestResourceMethodException e) {
					response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED).setBody(new HTTPBody(e.getMessage()));
					e.printStackTrace();
				} catch (GeneralServerException | BadRESTResourceMethodException e) {
					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR).setBody(new HTTPBody(e.getMessage()));
					e.printStackTrace();
				} catch (BadHTTPRequestException e) {
					response.setStatus(HTTPStatusCode.BAD_REQUEST).setBody(new HTTPBody(e.getMessage()));
					e.printStackTrace();
				}
			} else {
				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
			}
		} else {
			response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
		}
	}
}
