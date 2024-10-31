package com.apj.projects.coconut.http;

public enum HTTPContentTypes {

	APPLICATION_JSON("application/json", ".json"), TEXT_XML("text/xml", ".xml"), TEXT_HTML("text/html", ".html"),
	IMAGE_PNG("image/png", ".png"), IMAGE_JPEG("image/jpeg", ".jpeg"), IMAGE_JPG("image/jpg", ".jpg"),
	VIDEO_MP4("video/mp4", ".mp4"), TEXT_PLAIN("text/plain", ".");

	private String name;
	private String extension;

	private HTTPContentTypes(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

}
