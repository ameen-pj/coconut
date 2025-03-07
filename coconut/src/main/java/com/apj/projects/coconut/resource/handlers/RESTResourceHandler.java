package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.URLPath;
import com.apj.projects.coconut.http.URLPathMetadataExtractor;
import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.handlers.exceptions.GeneralServerException;
import com.apj.projects.coconut.resource.rest.RESTResourceMetadata;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceMethodException;
import com.apj.projects.coconut.resource.rest.exceptions.RestResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.UnsupportedRestResourceMethodException;
import com.apj.projects.coconut.utils.AnnotationScanner;
import com.apj.projects.coconut.utils.PropertyManager;
import com.apj.projects.coconut.utils.json.JSONObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@Handler("rest")
public class RESTResourceHandler extends ResourceHandler {

	private static Logger logger = LoggerFactory.getLogger(RequestManager.class);
	private static Reflections reflections = AnnotationScanner
			.getRestReflections(PropertyManager.getRestServicePackageName());

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
		return reflections.get(TypesAnnotated.with(RESTResourceMapping.class).asClass());
	}

	public void resourceMethodHandler(String resourceName, HTTPRequest request, HTTPResponse response)
			throws GeneralServerException, BadRESTResourceMethodException, UnsupportedRestResourceMethodException {

		HTTPRequestMethod httpRequestMethod = request.getHTTPRequestMethod();
		ArrayList<Method> resourceMethods = restResourcesDataMap.get(resourceName).getRequestMethodByRequest(request);

		Object resourceObject = restResourcesDataMap.get(resourceName).getRestResourceObject();

		// URL Query Parameters
		boolean isQueryParamsPresent = false;
		String queryParamString = request.getQueryParamString();
		HashMap<String, Object> requestQueryParams = new HashMap<String, Object>();
		if (queryParamString != null) {
			requestQueryParams = URLPath.getQueryParams(queryParamString);
			isQueryParamsPresent = true;
		}
		// Request Body [ONLY FOR JSON NOW]
		boolean isRequestBodyPresent = false;
		Optional<?> body = request.getBody();
		HashMap<Object, Object> object = null;
		if (body.isPresent()) {
			String jsonString = body.get().toString();
			try {
				object = JSONObjectMapper.getMapper().readValue(jsonString,
						new TypeReference<HashMap<Object, Object>>() {
						});
			} catch (JsonProcessingException e) {
				throw new GeneralServerException(e.getMessage());
			}
			isRequestBodyPresent = true;
		}

		// Finding the appropriate requestMethod
		Method requestMethod = null;
		ArrayList<Object> methodArgs = new ArrayList<Object>();

		for (Method resourceMethod : resourceMethods) {
			Parameter[] resourceMethodParameters = resourceMethod.getParameters();
			int parameterMatchCount = 0;

			for (Parameter resourceMethodParameter : resourceMethodParameters) {
				Annotation[] parameterAnnotations = resourceMethodParameter.getDeclaredAnnotations();
				for (Annotation parameterAnnotation : parameterAnnotations) {
					// if parameter key matches query string
					if (parameterAnnotation instanceof QueryParams && isQueryParamsPresent) {
						String queryParamKey = ((QueryParams) parameterAnnotation).value();
						if (requestQueryParams.keySet().contains(queryParamKey)) {
							methodArgs.add(requestQueryParams.get(((QueryParams) parameterAnnotation).value()));
							parameterMatchCount++;
						}
					}
					// Request Body
					else if (parameterAnnotation instanceof RequestBody && isRequestBodyPresent) {
						methodArgs.add(object);
						parameterMatchCount++;
					}
				}
			}
			if (parameterMatchCount == resourceMethodParameters.length) {
				requestMethod = resourceMethod;
				break;
			} else {
				methodArgs.clear();
			}
		}
		// Invoking the method and returning the value
		try {
			Optional<?> returnValue = Optional.ofNullable(requestMethod.invoke(resourceObject, methodArgs.toArray()));
			Annotation producesAnnotation = requestMethod.getDeclaredAnnotation(Produces.class);

			if (returnValue.isPresent()) {
				if (producesAnnotation != null) {
					HTTPContentTypes producesContentType = ((Produces) producesAnnotation).value();
					// Return JSON Response
					if (producesContentType == HTTPContentTypes.APPLICATION_JSON) {
						String json;
						try {
							json = JSONObjectMapper.getMapper().writeValueAsString(returnValue.get());
							response.setBody(new HTTPBody(json)).setContentType(HTTPContentTypes.APPLICATION_JSON);
						} catch (JsonProcessingException e) {
							throw new GeneralServerException(e.getMessage());
						}
					} else if (producesContentType == HTTPContentTypes.TEXT_PLAIN) {
						response.setBody(new HTTPBody((String) returnValue.get()))
								.setContentType(HTTPContentTypes.TEXT_PLAIN);
					} else {
						throw new GeneralServerException(producesContentType + " not implemented by this handler");
					}
				} else {
					throw new BadRESTResourceMethodException("No @produces annotation present");
				}
				// Setting appropriate status codes
				switch (httpRequestMethod) {
				case GET:
					response.setStatus(HTTPStatusCode.OK);
					break;
				case POST:
					response.setStatus(HTTPStatusCode.CREATED);
					break;
				case DELETE:
					response.setStatus(HTTPStatusCode.OK);
					break;
				case PUT:
					response.setStatus(HTTPStatusCode.OK);
				default:
					response.setStatus(HTTPStatusCode.OK);
					break;
				}
			} else {
				response.setStatus(HTTPStatusCode.OK);
			}
		} catch (IllegalAccessException e) {
			throw new GeneralServerException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new BadRESTResourceException(e.getCause().getMessage());
		}
	}

	@Override
	public void handle(HTTPRequest request, HTTPResponse response) throws Exception {

		String resourceName = URLPathMetadataExtractor.getResourcePath(request.getHTTPURLPath());

		if (resourceName != null) {
			if (restResourcesDataMap.containsKey(resourceName)) {
				try {
					resourceMethodHandler(resourceName, request, response);
				} catch (UnsupportedRestResourceMethodException e) {
					response.setStatus(HTTPStatusCode.NOT_IMPLEMENTED).setBody(new HTTPBody(e.getMessage()));
					logger.error("UnsupportedRestResourceMethodException", e);
				} catch (GeneralServerException | BadRESTResourceMethodException e) {
					response.setStatus(HTTPStatusCode.INTERNAL_SERVER_ERROR).setBody(new HTTPBody(e.getMessage()));
					logger.error("GeneralServerException | BadRESTResourceMethodException", e);
				}
			} else {
				response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
			}
		} else {
			response.setStatus(HTTPStatusCode.NOT_FOUND).setBody(new HTTPBody("Resource could not be found"));
		}
	}
}
