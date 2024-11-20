package com.apj.projects.coconut.resource.handlers;

import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPResponse;

public abstract class ResourceHandler {

	private String resourceType;

	public abstract void handle(HTTPRequest request, HTTPResponse response) throws Exception;

	// Getters
	public String getResourceType() {
		return resourceType;
	}

	// Setters
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

}
