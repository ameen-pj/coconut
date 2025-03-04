package com.apj.projects.coconut.http;

import java.io.File;
import java.util.Optional;

public class HTTPBody {

	private Optional<?> body;
	private long size;
	private boolean isMediaType;

	public HTTPBody(String body) {
		this.isMediaType = false;
		this.body = Optional.ofNullable(body);
		this.size = body.length();
	}

	public HTTPBody(File file) {
		this.isMediaType = true;
		this.body = Optional.ofNullable(file);
		this.size = file.length();
	}

	public boolean isMediaType() {
		return isMediaType;
	}

	public Optional<?> getContent() {
		return body;
	}

	public long getSize() {
		return size;
	}

}
