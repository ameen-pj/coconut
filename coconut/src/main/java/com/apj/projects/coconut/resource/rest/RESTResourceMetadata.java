package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.apj.projects.coconut.http.URLPath;
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

	private Object restResourceObject;
	private String restResourceName;

	private HashMap<Pair<HTTPRequestMethod, URLPath>, Method> requestMethodMap;

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

	public Method getRequestMethodByPair(Pair<HTTPRequestMethod, URLPath> pair)
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
			restResourceObject = new ObjectCreator(restResourceClass).createObject();
			// Setting name
			String name = restResourceClass.getAnnotation(RESTResourceMapping.class).value();
			restResourceName = name == null ? restResourceClass.getName() : name;
		} catch (ObjectCreatorException e) {
			throw new RestResourceException("Could not create restResource object : " + e.getMessage());
		}
	}

	private void mapRequestMethods() {

		requestMethodMap = new HashMap<Pair<HTTPRequestMethod, URLPath>, Method>();
		Method[] restResourceMethods = restResourceClass.getDeclaredMethods();
		// Default
		String path = "/";
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
				} else if (annotation instanceof Path) {
					path = "/rest/" + restResourceName + "/" + ((Path) annotation).value();
				}
			}
			if (path != null && requestMethodType != null) {
				Pair<HTTPRequestMethod, URLPath> pair = new Pair<HTTPRequestMethod, URLPath>(requestMethodType,
						new URLPath(path));

				if (!requestMethodMap.containsKey(pair))
					requestMethodMap.put(pair, m);
				else
					System.out.println(pair + " already exists with the same path. Neglecting current pair");
			}
		}
	}

}
