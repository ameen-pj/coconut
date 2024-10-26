package com.apj.projects.coconut.utils;

public enum HTTPContentTypes {

	APPLICATION_JSON("application/json"), TEXT_PLAIN("text/html"), TEXT_XML("text/xml"), TEXT_HTML("text/html"),
	IMAGE_PNG("image/png"), IMAGE_JPEG("image/jpeg"), IMAGE_JPG("image/jpg");

	private String name;

	private HTTPContentTypes(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
