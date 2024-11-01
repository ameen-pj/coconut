package com.apj.projects.coconut.core;

public class Server {

	private Server() {

		System.out.println("====Coconut====");

		try {

			ServerSocketManager.openServerSocket();
			ServerSocketManager.listenForConnections();
		} catch (Exception e) {
			System.err.println("[ERROR]: " + e.getMessage());
		} finally {
			ServerSocketManager.closeServerSocket();
		}

	}

	public static void start() {
		new Server();
	}
}
