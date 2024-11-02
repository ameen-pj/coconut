package com.apj.projects.coconut.http;

import java.io.File;
import java.util.Optional;

public class HTTPBody {

	private Optional<?> body;
	private boolean isMediaType;

	public HTTPBody(String body) {
		this.isMediaType = false;
		this.body = Optional.ofNullable(body);
	}

	public HTTPBody(File file) {
		this.isMediaType = true;
		this.body = Optional.ofNullable(file);
	}

	public boolean isMediaType() {
		return isMediaType;
	}

	public Optional<?> getContent() {
		return body;
	}

}
