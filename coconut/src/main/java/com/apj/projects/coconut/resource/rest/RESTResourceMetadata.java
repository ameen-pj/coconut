package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import com.apj.projects.coconut.http.enums.HTTPRequestMethod;
import com.apj.projects.coconut.resource.rest.annotations.DELETE;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.PUT;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceException;
import com.apj.projects.coconut.resource.rest.exceptions.BadRESTResourceMethodException;

public class RESTResourceMetadata {

	private Class<?> restEntity;
	private Class<?> restResourceClass;
	private String restResourceName;
	private Object restResourceObject;
	private HashMap<HTTPRequestMethod, Method> requestMethodMap;

	public RESTResourceMetadata(Class<?> restResource) {

		try {

			requestMethodMap = new HashMap<HTTPRequestMethod, Method>();

			restEntity = ((ParameterizedType) restResource.getGenericSuperclass()).getActualTypeArguments()[0]
					.getClass();
			restResourceClass = restResource;
			createNewRestResourceObject();
			setRestResourceName();
			mapMethodsByRequestType();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new BadRESTResourceException(
					"Could not instantiate REST Resource class: " + restResource.getCanonicalName());
		}

	}

	public String getRestResourceName() {
		return restResourceName;
	}

	public Object getRestResourceObject() {
		return restResourceObject;
	}

	public Method getMethodByHTTPRequestMethod(HTTPRequestMethod requestMethod) {
		return requestMethodMap.get(requestMethod);
	}

	public void invokeMethodByHTTPRequestMethod(HTTPRequestMethod requestMethod) throws BadRESTResourceMethodException {

		Method method = getMethodByHTTPRequestMethod(requestMethod);
		if (method != null) {
			try {
				method.invoke(getRestResourceObject());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new BadRESTResourceMethodException("Could not invoke REST method for this request");
			}
		}
	}

	private void createNewRestResourceObject() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.restResourceObject = restResourceClass.getDeclaredConstructor().newInstance();

	}

	private void setRestResourceName() {

		String name = restResourceClass.getAnnotation(RESTResourceMapping.class).value();
		this.restResourceName = name == null ? restResourceClass.getName() : name;

	}

	private void mapMethodsByRequestType() {

		Method[] restResourceMethods = restResourceClass.getMethods();

		for (Method m : restResourceMethods) {
			for (Annotation annotation : m.getAnnotations()) {
				if (annotation instanceof GET) {
					requestMethodMap.put(HTTPRequestMethod.GET, m);
				} else if (annotation instanceof POST) {
					requestMethodMap.put(HTTPRequestMethod.POST, m);
				} else if (annotation instanceof DELETE) {
					requestMethodMap.put(HTTPRequestMethod.DELETE, m);
				} else if (annotation instanceof PUT) {
					requestMethodMap.put(HTTPRequestMethod.PUT, m);
				}
			}
		}
	}
}
