package com.apj.projects.coconut.http;

import java.util.ArrayList;

public class URLPathMetadataExtractor {

	public static String getResourceType(URLPath urlPath) {
		return urlPath.getPathPartByIndex(0);
	}

	public static String getResourcePath(URLPath urlPath) {

		ArrayList<String> pathParts = urlPath.getPathParts();
		int length = pathParts.size();
		StringBuffer resourcePath = new StringBuffer();

		if (length > 1) {
			return String.join("/", pathParts.subList(1, length));
		} else {
			return null;
		}
	}

}
