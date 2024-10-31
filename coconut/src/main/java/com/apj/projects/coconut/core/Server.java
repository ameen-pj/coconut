package com.apj.projects.coconut.core;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public Server() {
		System.out.println("[INFO]: Initializing server ...");

		ServerSocket serverSocket = ServerManager.getSocket();
		listenForConnections(serverSocket);

		ServerManager.closeSocket();

	}

	private void listenForConnections(ServerSocket serverSocket) {

		System.out.println("[INFO]: Listening for Connections ...");

		while (true) {
			try {
				Socket socket = serverSocket.accept();

//				HttpRequest request = new HttpRequest(socket.getInputStream());
//				HttpResponse response = new HttpResponse(socket.getOutputStream());
//
//				response.sendFile("book.pdf", HTTPStatusCodes.OK);

				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
