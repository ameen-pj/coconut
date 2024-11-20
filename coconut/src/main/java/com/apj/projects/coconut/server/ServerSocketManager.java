package com.apj.projects.coconut.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.apj.projects.coconut.concurent.ThreadpoolService;
import com.apj.projects.coconut.concurent.exceptions.ThreadpoolServiceException;
import com.apj.projects.coconut.resource.handlers.RequestManager;

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
		ThreadpoolService threadpoolService = ThreadpoolService.getThreadPoolService();

		while (true) {
			try {
				Socket socket = serverSocket.accept();

				RequestManager reqHandler = new RequestManager(socket, socket.getInputStream(),
						socket.getOutputStream());

				// Handles the request and closes the socket
				try {
					threadpoolService.execute(reqHandler);
				} catch (ThreadpoolServiceException e) {
					System.err.println("[ERROR]: " + e.getMessage());
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
