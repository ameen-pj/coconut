package com.apj.projects.coconut.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.apj.projects.coconut.http.exceptions.BadURLException;

public class HTTPURLPath {

	private String pathString;
	private String[] pathParts;

	public HTTPURLPath(String url) {
		this.pathString = url;
		this.pathParts = url.substring(url.indexOf("/") + 1).split("/");
	}

	public String[] getPathParts() {
		return pathParts;
	}

	public String getPathString() {
		return pathString;
	}

	public boolean equals(HTTPURLPath path) {
		return Arrays.equals(pathParts, path.getPathParts());
	}

	public boolean isParentPathOf(HTTPURLPath childPath) {

		return childPath.getPathString().indexOf(this.getPathString()) == 0;

	}

	public String getResourceType() throws BadURLException {
		if (pathParts.length > 0) {
			return pathParts[0];
		}
		throw new BadURLException("Cannot extract ResourceType from urlPath: " + pathString);
	}

	public String getResourceName() throws BadURLException {
		if (pathParts.length > 1) {
			return pathParts[1];
		}
		throw new BadURLException("Cannot extract ResourceName from urlPath: " + pathString);
	}

	public HashMap<String, String> getParams(String urlExpression) {

		HashMap<String, String> params = new HashMap<>();

		String[] urlExpressionParts = urlExpression.substring(urlExpression.indexOf("/") + 1).split("/");
		for (int i = 0; i < urlExpressionParts.length; i++) {

			Pattern pattern = Pattern.compile("\\{(.*?)\\}");
			Matcher matcher = pattern.matcher(urlExpressionParts[i]);

			while (matcher.find()) {
				if (i < pathParts.length) {
					params.put(matcher.group(1), pathParts[i]);
				}

			}
		}
		return params;

	}
}
