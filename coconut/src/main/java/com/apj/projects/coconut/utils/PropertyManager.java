package com.apj.projects.coconut.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.concurent.ThreadType;

public class PropertyManager {

	private static Properties coconutProps = new Properties();
	private static final String FILE_NAME = "coconut.properties";

	private static Logger logger = LoggerFactory.getLogger(PropertyManager.class);

	static {
		loadProps();
	}

	private static void loadProps() {

		URL classPathURL = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);

		if (classPathURL == null) {
			throw new RuntimeException("Could not find coconut.properties in classpath");
		} else {
			String classPathFile = classPathURL.getPath();
			try {
				coconutProps.load(new FileInputStream(classPathFile));
			} catch (IOException e) {
				logger.error("PropertyManager Error", e);
			}
		}
	}

	public static int getPort() {
		Object port = coconutProps.get("port");
		if (port == null) {
			return 8080;
		}
		return Integer.valueOf((String) port);
	}

	public static int getNThreads() {
		Object nThreads = coconutProps.get("n_threads");
		if (nThreads == null) {
			return 10;
		}
		return Integer.valueOf((String) nThreads);
	}

	public static ThreadType getThreadType() {
		Object threadType = coconutProps.get("thread_type");
		if (threadType == null) {
			return ThreadType.VIRTUAL_THREAD;
		}
		ThreadType tt = ThreadType.valueOf((String) threadType);
		return tt;
	}

	public static String getRestServicePackageName() {
		Object restServicePackageName = coconutProps.get("rest_service_package_name");
		if (restServicePackageName == null) {
			logger.error("No rest_service_package_name defined in coconut.properties. Defaulting to com");
			return "com";
		}
		return (String) restServicePackageName;
	}
}
