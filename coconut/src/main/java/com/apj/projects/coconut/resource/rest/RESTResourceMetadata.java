package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.apj.projects.coconut.http.HTTPRequest;
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

	private Class<?> restResourceClass;
	private Object restResourceObject;
	private String restResourceName;

	private HashMap<HTTPRequestMethod, ArrayList<Method>> requestMethodMap;

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

	public ArrayList<Method> getRequestMethodByRequest(HTTPRequest request)
			throws UnsupportedRestResourceMethodException {

		HTTPRequestMethod requestMethodType = request.getHTTPRequestMethod();
		ArrayList<Method> resourceMethods = requestMethodMap.get(requestMethodType);
		if (resourceMethods.size() == 0) {
			throw new UnsupportedRestResourceMethodException(
					requestMethodType + " not implemented for " + getRestResourceName());
		}
		return resourceMethods;
	}

	private String getQualifiedRestResourceName() {
		String resourceName = "";
		Class<?> currentClass = restResourceClass;
		Annotation ann = currentClass.getAnnotation(RESTResourceMapping.class);
		if (ann != null) {
			String name = ((RESTResourceMapping) ann).value();
			resourceName = (name == null ? currentClass.getName() : name);
		}
		return resourceName;
	}

	private void createResourceObject() throws RestResourceException {
		// Initializing restResource object
		try {
			restResourceObject = new ObjectCreator(restResourceClass).createObject();
			restResourceName = getQualifiedRestResourceName();
		} catch (ObjectCreatorException e) {
			throw new RestResourceException("Could not create restResource object : " + e.getMessage());
		}
	}

	private void mapRequestMethods() {

		requestMethodMap = new HashMap<HTTPRequestMethod, ArrayList<Method>>();
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
				ArrayList<Method> methods = requestMethodMap.getOrDefault(requestMethodType, new ArrayList<Method>());
				methods.add(m);
				if (methods.size() <= 1)
					requestMethodMap.put(requestMethodType, methods);
			}
		}
	}
}
