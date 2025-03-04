package com.apj.projects.coconut.utils;

import org.reflections.Reflections;

public class AnnotationScanner {

	private static Reflections coconutReflections;
	private static Reflections restReflections;

	public static Reflections getCoconutReflections() {

		if (coconutReflections == null) {
			coconutReflections = new Reflections("com.apj.projects.coconut");
		}
		return coconutReflections;
	}

	public static Reflections getRestReflections(String restServicePackageName) {

		if (restReflections == null) {
			restReflections = new Reflections(restServicePackageName);
		}
		return restReflections;
	}

}
