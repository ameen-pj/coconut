package com.apj.projects.coconut.utils;

import org.reflections.Reflections;

public class AnnotationScanner {

	private static Reflections reflections;

	public static Reflections getReflections() {

		if (reflections == null) {
			reflections = new Reflections("com.apj.projects.coconut");
		}
		return reflections;
	}

}
