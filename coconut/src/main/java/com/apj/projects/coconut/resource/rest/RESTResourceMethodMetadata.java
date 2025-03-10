package com.apj.projects.coconut.resource.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class RESTResourceMethodMetadata {

	private Method restResourceMethod;
	private Parameter[] restResourceParameters;
	private ArrayList<Annotation[]> restResourceParameterAnnotations = new ArrayList<Annotation[]>();

	public RESTResourceMethodMetadata(Method method) {

		restResourceMethod = method;
		restResourceParameters = restResourceMethod.getParameters();

		for (Parameter restResourceParameter : restResourceParameters) {
			Annotation[] parameterAnnotations = restResourceParameter.getDeclaredAnnotations();
			restResourceParameterAnnotations.add(parameterAnnotations);
		}

	}

	public Method getRestResourceMethod() {
		return restResourceMethod;
	}

	public Parameter[] getRestResourceMethodParameters() {
		return restResourceParameters;
	}

	public ArrayList<Annotation[]> getRestResourceMethodParameterAnnotation() {
		return restResourceParameterAnnotations;
	}

}
