package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.resource.rest.annotations.DELETE;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.PUT;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.RestResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.UnsupportedRestResourceMethodException;
import com.apj.projects.coconut.util.exceptions.ObjectCreatorException;
import com.apj.projects.coconut.utils.ObjectCreator;

public class RESTResourceMetadata {

	private static Logger logger = LoggerFactory.getLogger(RESTResourceMetadata.class);

	private Class<?> restResourceClass;
	private Object restResourceObject;
	private String restResourceName;

	private HashMap<HTTPRequestMethod, Method> requestMethodMap;

	public RESTResourceMetadata(Class<?> restResourceClass) throws RestResourceException {

		this.restResourceClass = restResourceClass;

		createResourceObject();
		mapRequestMethods();

	}

	public String getRestResourceName() {
		return restResourceName;
	}

	public Class<?> getRestResourceClass() {
		return restResourceClass;
	}

	public Object getRestResourceObject() {
		return restResourceObject;
	}

	public Method getRequestMethodByMethodType(HTTPRequestMethod httpRequestMethod)
			throws UnsupportedRestResourceMethodException {

		Method method = requestMethodMap.get(httpRequestMethod);
		if (method == null) {
			throw new UnsupportedRestResourceMethodException(
					httpRequestMethod + " not implemented for " + getRestResourceName());
		}
		return method;
	}

	private void createResourceObject() throws RestResourceException {
		// Initializing restResource object
		try {
			restResourceObject = new ObjectCreator(restResourceClass).createObject();
			// Setting name
			String name = restResourceClass.getAnnotation(RESTResourceMapping.class).value();
			restResourceName = name == null ? restResourceClass.getName() : name;
		} catch (ObjectCreatorException e) {
			throw new RestResourceException("Could not create restResource object : " + e.getMessage());
		}
	}

	private void mapRequestMethods() {

		requestMethodMap = new HashMap<HTTPRequestMethod, Method>();
		Method[] restResourceMethods = restResourceClass.getDeclaredMethods();
		HTTPRequestMethod requestMethodType = null;

		for (Method m : restResourceMethods) {
			Annotation[] annotations = m.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof GET) {
					requestMethodType = HTTPRequestMethod.GET;
				} else if (annotation instanceof POST) {
					requestMethodType = HTTPRequestMethod.POST;
				} else if (annotation instanceof DELETE) {
					requestMethodType = HTTPRequestMethod.DELETE;
				} else if (annotation instanceof PUT) {
					requestMethodType = HTTPRequestMethod.PUT;
				}
			}
			if (requestMethodType != null) {
				if (!requestMethodMap.containsKey(requestMethodType))
					requestMethodMap.put(requestMethodType, m);
				else
					logger.info(requestMethodType + " already exists for the resource " + getRestResourceName());
			}
		}
	}

//	private void mapRequestMethodsv1() {
//
//		requestMethodMap = new HashMap<Pair<HTTPRequestMethod, URLPath>, Method>();
//		Method[] restResourceMethods = restResourceClass.getDeclaredMethods();
//		// Default
//		String path = "/";
//		HTTPRequestMethod requestMethodType = null;
//
//		for (Method m : restResourceMethods) {
//			Annotation[] annotations = m.getDeclaredAnnotations();
//			for (Annotation annotation : annotations) {
//				if (annotation instanceof GET) {
//					requestMethodType = HTTPRequestMethod.GET;
//				} else if (annotation instanceof POST) {
//					requestMethodType = HTTPRequestMethod.POST;
//				} else if (annotation instanceof DELETE) {
//					requestMethodType = HTTPRequestMethod.DELETE;
//				} else if (annotation instanceof PUT) {
//					requestMethodType = HTTPRequestMethod.PUT;
//				} else if (annotation instanceof Path) {
//					path = "/rest/" + restResourceName + "/" + ((Path) annotation).value();
//				}
//			}
//			if (path != null && requestMethodType != null) {
//				Pair<HTTPRequestMethod, URLPath> pair = new Pair<HTTPRequestMethod, URLPath>(requestMethodType,
//						new URLPath(path));
//
//				if (!requestMethodMap.containsKey(pair))
//					requestMethodMap.put(pair, m);
//				else
//					logger.info(pair + " already exists with the same path. Neglecting current pair");
//			}
//		}
//	}
}
