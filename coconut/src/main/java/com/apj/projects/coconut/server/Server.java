package com.apj.projects.coconut.server;

public class Server {

	private Server() {

		System.out.println("====Coconut====");

		try {

			ServerSocketManager.openServerSocket();
			ServerSocketManager.listenForConnections();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[ERROR]: " + e.getMessage());
		} finally {
			ServerSocketManager.closeServerSocket();
		}

	}

	public static void start() {
		new Server();
	}
}
