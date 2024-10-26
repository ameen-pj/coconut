package com.apj.projects.coconut.core;

import java.io.IOException;
import java.net.ServerSocket;

// Singleton Socket object
public class ServerManager {

	private static ServerSocket serverSocket;

	private ServerManager() {
		try {
			serverSocket = new ServerSocket(8080);
			System.out.println("[INFO]: Socket opened at port: " + 8080);
		} catch (IOException e) {
			System.out.println("[ERROR]: " + e.getMessage());
			System.exit(0);
		}
	}

	static ServerSocket getSocket() {
		if (serverSocket == null) {
			new ServerManager();
		}
		return serverSocket;
	}

	static void closeSocket() {
		if (serverSocket == null) {
			System.out.println("[INFO]: No Socket to close");
		}
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
				System.out.println("[INFO]: Socket closed");
			} catch (IOException e) {
				System.out.println("[ERROR]: " + e.getMessage());
				System.exit(0);
			}
		}
		serverSocket = null;
	}
}
