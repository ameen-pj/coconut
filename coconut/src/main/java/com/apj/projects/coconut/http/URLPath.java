package com.apj.projects.coconut.http;

import java.util.Arrays;
import java.util.HashMap;

import com.apj.projects.coconut.utils.GeneralUtils;

public class URLPath {

	private String pathString;
	private String[] pathParts;

	public URLPath(String url) {
		this.pathString = url;
		this.pathParts = url.substring(url.indexOf("/") + 1).split("/");
	}

	public String[] getPathParts() {
		return pathParts;
	}

	public String getPathString() {
		return pathString;
	}

	public boolean equals(URLPath path) {
		return Arrays.equals(pathParts, path.getPathParts());
	}

	public boolean isChildPathOf(String parentPath) {
		return pathString.indexOf(parentPath) == 0;
	}

	public String getPathPartByIndex(int index) {

		if (index >= pathParts.length) {
			return null;
		}
		return getPathParts()[index];
	}

	public boolean containsQueryParams() {
		return pathString.contains("?");
	}

	public HashMap<String, Object> getQueryParams() {

		HashMap<String, Object> params = new HashMap<>();

		if (containsQueryParams()) {
			String paramString = pathString.split("\\?")[1];
			String[] paramKeyValue = paramString.split("\\&");

			for (String keyValueString : paramKeyValue) {
				if (keyValueString.contains("=")) {
					String[] keyValueArray = keyValueString.split("=");
					String key = keyValueArray[0];
					String value = keyValueArray[1];
					if (GeneralUtils.isNumeric(value))
						params.put(keyValueArray[0], Integer.parseInt(keyValueArray[1]));
					else
						params.put(keyValueArray[0], keyValueArray[1]);
				}
			}
		}
		return params;
	}
}
