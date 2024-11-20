package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import com.apj.projects.coconut.http.HTTPURLPath;
import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.resource.handlers.annotations.Path;
import com.apj.projects.coconut.resource.rest.annotations.DELETE;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.PUT;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.RestResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.UnsupportedRestResourceMethodException;
import com.apj.projects.coconut.util.exceptions.ObjectCreatorException;
import com.apj.projects.coconut.utils.ObjectCreator;
import com.apj.projects.coconut.utils.Pair;

public class RESTResourceMetadata {

	private Class<?> restResourceClass;
	private Class<?> restEntityClass;

	private Object restResourceObject;
	private String restResourceName;

	private HashMap<Pair<HTTPRequestMethod, HTTPURLPath>, Method> requestMethodMap;

	public RESTResourceMetadata(Class<?> restResourceClass) throws RestResourceException {

		this.restResourceClass = restResourceClass;
		this.restEntityClass = ((ParameterizedType) restResourceClass.getGenericSuperclass())
				.getActualTypeArguments()[0].getClass();

		createResourceObject();
		mapRequestMethods();

	}

	public String getRestResourceName() {
		return restResourceName;
	}

	public Class<?> getRestResourceClass() {
		return restResourceClass;
	}

	public Class<?> getRestEntityClass() {
		return restEntityClass;
	}

	@SuppressWarnings("unchecked")
	public RESTResource<? extends Entity> getRestResourceObject() {
		return (RESTResource<? extends Entity>) restResourceObject;
	}

	public Method getRequestMethodByPair(Pair<HTTPRequestMethod, HTTPURLPath> pair)
			throws UnsupportedRestResourceMethodException {
		Method method = requestMethodMap.get(pair);
		if (method == null) {
			throw new UnsupportedRestResourceMethodException(
					pair.getKey() + ":" + pair.getValue().getPathString() + " not implemented");
		}
		return method;
	}

	private void createResourceObject() throws RestResourceException {
		// Initializing restResource object
		try {
			restResourceObject = new ObjectCreator(restResourceClass);
			// Setting name
			String name = restResourceClass.getAnnotation(RESTResourceMapping.class).value();
			restResourceName = name == null ? restResourceClass.getName() : name;
		} catch (ObjectCreatorException e) {
			throw new RestResourceException("Could not create restResource object : " + e.getMessage());
		}
	}

	private void mapRequestMethods() {

		requestMethodMap = new HashMap<Pair<HTTPRequestMethod, HTTPURLPath>, Method>();
		Method[] restResourceMethods = restResourceClass.getMethods();
		// Default
		String path = "/";
		HTTPRequestMethod requestMethodType = null;

		for (Method m : restResourceMethods) {
			Annotation[] annotations = m.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof GET) {
					requestMethodType = HTTPRequestMethod.GET;
				} else if (annotation instanceof POST) {
					requestMethodType = HTTPRequestMethod.POST;
				} else if (annotation instanceof DELETE) {
					requestMethodType = HTTPRequestMethod.DELETE;
				} else if (annotation instanceof PUT) {
					requestMethodType = HTTPRequestMethod.PUT;
				} else if (annotation instanceof Path) {
					path = restResourceName + ((Path) annotation).value();
				}
			}

			if (path != null && requestMethodType != null) {
				requestMethodMap.put(new Pair<HTTPRequestMethod, HTTPURLPath>(requestMethodType, new HTTPURLPath(path)),
						m);
			}
		}
	}

}
