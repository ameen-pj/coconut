package com.apj.projects.coconut.resource.handlers;

import com.apj.projects.coconut.resource.handlers.annotations.Handler;

@Handler("file")
public class FileResourceHandler {

	public static FileResourceHandler fileHandler;

	private FileResourceHandler() {

	}

	private static FileResourceHandler getFileHandler() {
		if (fileHandler == null) {
			fileHandler = new FileResourceHandler();
		}
		return fileHandler;
	}

}
