package com.apj.projects.coconut.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Singleton Socket object
public class ServerSocketManager {

	private static ServerSocket serverSocket;

	public static void openServerSocket() {
		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(8080);
				System.out.println("[INFO]: Socket opened at port: " + 8080);
			} catch (IOException e) {
				System.out.println("[ERROR]: " + e.getMessage());
				System.exit(0);
			}
		}
	}

	public static void listenForConnections() {

		System.out.println("[INFO]: Listening for connections ...");

		while (true) {

			try {
				Socket socket = serverSocket.accept();

				if (!socket.isClosed()) {
					socket.close();
				}

			} catch (IOException e) {
				System.err.println("[ERROR]: " + e.getMessage());
			}
		}
	}

	public static void closeServerSocket() {
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
