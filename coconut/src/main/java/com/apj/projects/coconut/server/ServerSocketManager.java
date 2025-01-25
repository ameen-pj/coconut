package com.apj.projects.coconut.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.concurent.ThreadpoolService;
import com.apj.projects.coconut.concurent.exceptions.ThreadpoolServiceException;
import com.apj.projects.coconut.resource.handlers.RequestManager;

// Singleton Socket object
public class ServerSocketManager {

	private static Logger logger = LoggerFactory.getLogger(ServerSocketManager.class);

	private static ServerSocket serverSocket;

	public static void openServerSocket() {
		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(8080);
				logger.info("Socket opened at port: " + 8080);
			} catch (IOException e) {
				logger.error("ServerSocker Error" + e);
				System.exit(0);
			}
		}
	}

	public static void listenForConnections() {

		logger.info("Listening for connections ...");
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
					logger.error("ThreadpoolService Error", e);
				}

			} catch (IOException e) {
				logger.error("ServerSocketError", e);
			}
		}
	}

	public static void closeServerSocket() {
		if (serverSocket == null) {
			logger.info("ServerSocketError: No Socket to Close");
		}
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
				logger.info("Socket closed");
			} catch (IOException e) {
				logger.error("ServerSocketError", e);
				System.exit(0);
			}
		}
		serverSocket = null;
	}
}
