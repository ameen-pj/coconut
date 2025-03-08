package com.apj.projects.coconut.resource.handlers;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.apj.projects.coconut.http.exceptions.BadHTTPHeadersException;
import com.apj.projects.coconut.http.exceptions.BadHTTPRequestException;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Handler;
import com.apj.projects.coconut.resource.handlers.exceptions.GeneralServerException;
import com.apj.projects.coconut.resource.rest.RESTResourceMetadata;
import com.apj.projects.coconut.resource.rest.RESTResourceMethodMetadata;
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
			throws GeneralServerException, BadRESTResourceMethodException, UnsupportedRestResourceMethodException,
			BadHTTPRequestException {

		HTTPRequestMethod httpRequestMethod = request.getHTTPRequestMethod();
		RESTResourceMetadata restResourceMetadata = restResourcesDataMap.get(resourceName);
		ArrayList<Method> resourceMethods = restResourceMetadata.getRequestMethodByRequest(request);

		Object resourceObject = restResourcesDataMap.get(resourceName).getRestResourceObject();

		// URL Query Parameters
		boolean isQueryParamsPresent = false;
		String queryParamString = request.getQueryParamString();
		HashMap<String, Object> requestQueryParams = new HashMap<String, Object>();
		if (queryParamString != null) {
			requestQueryParams = URLPath.getQueryParams(queryParamString);
			isQueryParamsPresent = true;
		}
		// Request Body
		boolean isRequestBodyPresent = false;
		HTTPContentTypes bodyType = null;
		try {
			List<String> contentType = request.getHeaderByField("CONTENT-TYPE");
			// Get the first content-type
			if (contentType != null && contentType.size() > 0) {
				isRequestBodyPresent = true;
				for (HTTPContentTypes t : HTTPContentTypes.values()) {
					if (t.getName().contains(contentType.get(0))) {
						bodyType = t;
						break;
					}
				}
			}
		} catch (BadHTTPHeadersException e) {
			logger.error(e.getMessage());
		}

		// Finding the appropriate requestMethod
		Method requestMethod = null;
		ArrayList<Object> methodArgs = new ArrayList<Object>();

		for (Method resourceMethod : resourceMethods) {
			RESTResourceMethodMetadata restResourceMethodMetadata = restResourceMetadata
					.getRestResourceMethodMetadataByMethod(resourceMethod);
			Parameter[] resourceMethodParameters = restResourceMethodMetadata.getRestResourceMethodParameters();
			int parameterMatchCount = 0;
			// For non parameterized methods
			if (resourceMethodParameters.length == 0) {
				if (isQueryParamsPresent || isRequestBodyPresent)
					parameterMatchCount = -1;
			}

			for (int i = 0; i < resourceMethodParameters.length; i++) {
				Annotation[] parameterAnnotations = restResourceMethodMetadata
						.getRestResourceMethodParameterAnnotation().get(i);
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

						if (bodyType == HTTPContentTypes.APPLICATION_JSON) {
							HashMap<Object, Object> body;
							try {
								Object objBody = request.getBody(HTTPContentTypes.APPLICATION_JSON);
								if (objBody instanceof HashMap<?, ?>) {
									body = (HashMap<Object, Object>) objBody;
									methodArgs.add(body);
									parameterMatchCount++;
								}

							} catch (JsonProcessingException e) {
								throw new GeneralServerException(e.getMessage());
							}
						} else {
							throw new BadHTTPRequestException(bodyType + " not supported/");
						}
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
		if (requestMethod != null) {
			// Invoking the method and returning the value
			try {
				Optional<?> returnValue = Optional
						.ofNullable(requestMethod.invoke(resourceObject, methodArgs.toArray()));
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
		} else {
			throw new BadHTTPRequestException("Could not find restResourceHandler method with appropriate parameters");
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
