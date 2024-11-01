package com.apj.projects.coconut.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class FileSender {

	private File file;
	private BufferedInputStream in;

	public FileSender(String fileName) {
		this.file = new File(fileName);
	}

	public long getFileSize() {
		return file.length();
	}

	public void ready() throws FileNotFoundException {
		this.in = new BufferedInputStream(new FileInputStream(file), 1024 * 4);
	}

	public void manualCopy(OutputStream out) throws IOException {

		byte[] buff = new byte[1024 * 4];
		int count = 0;

		while ((count = in.read(buff)) >= 0) {
			out.write(buff, 0, count);
		}

	}

	public void copyTo(OutputStream out) throws IOException {
		in.transferTo(out);
	}

}
