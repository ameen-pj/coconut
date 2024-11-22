package com.apj.projects.coconut.http;

import java.util.ArrayList;
import java.util.HashMap;

import com.apj.projects.coconut.utils.GeneralUtils;

public class URLPath {

	private StringBuilder pathString;
	private ArrayList<String> pathParts;

	public URLPath(String url) {
		this.pathParts = new ArrayList<String>();
		this.pathString = new StringBuilder();

		String[] splitUrl = url.split("/");
		for (int i = 0; i < splitUrl.length; i++) {
			String s = splitUrl[i];
			if (s.length() > 0) {
				pathParts.add(s);
			}
			if (i == splitUrl.length - 1) {
				pathString.append(s);
			} else {
				pathString.append(s + "/");
			}
		}
	}

	public ArrayList<String> getPathParts() {
		return pathParts;
	}

	public String getPathString() {
		return pathString.toString();
	}

	public boolean equals(URLPath path) {
		return pathParts.equals(path.getPathParts());
	}

	public boolean isChildPathOf(String parentPath) {
		return pathString.indexOf(parentPath) == 0;
	}

	public String getPathPartByIndex(int index) {

		if (index >= pathParts.size()) {
			return null;
		}
		return getPathParts().get(index);
	}

	public boolean containsQueryParams() {
		return getPathString().contains("?");
	}

	public void join(URLPath urlPath) {

		int length = urlPath.getPathParts().size();

		if (urlPath.getPathParts().size() > 0) {
			pathString.append("/");
		}
		for (int i = 0; i < length; i++) {
			String s = urlPath.getPathParts().get(i);
			pathParts.add(s);
			if (i == length - 1) {
				pathString.append(s);
			} else {
				pathString.append(s + "/");
			}
		}
	}

	@Deprecated
	public String getAbsolutePath() {
		String s = getPathString();
		if (containsQueryParams()) {
			return s.substring(0, s.indexOf("?"));
		}
		return s;
	}

	public static HashMap<String, Object> getQueryParams(String queryParamString) {

		HashMap<String, Object> params = new HashMap<>();
		String[] paramKeyValue = queryParamString.split("\\&");

		for (String keyValueString : paramKeyValue) {
			if (keyValueString.contains("=")) {
				String[] keyValueArray = keyValueString.split("=");
				String key = keyValueArray[0];
				String value = keyValueArray[1];
				if (GeneralUtils.isNumeric(value))
					params.put(key, Integer.parseInt(value));
				else
					params.put(key, value);
			}
		}
		return params;
	}

	@Override
	public String toString() {
		return pathString.toString();
	}

	@Override
	public int hashCode() {
		return pathParts.hashCode();
	}

	public static void main(String[] args) {
		URLPath p1 = new URLPath("/rest/student/search?id=10");
		System.out.println(p1.getPathString());
//		p1.join(new URLPath("/search?id=10"));
//		System.out.println(p1.getPathString());

//		URLPath p2 = new URLPath("");
//		System.out.println(p1.getPathString());
//		System.out.println(p2.getPathString());
//		System.out.println(p2.equals(p1));

	}
}