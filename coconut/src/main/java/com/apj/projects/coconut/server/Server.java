package com.apj.projects.coconut.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static Logger logger = LoggerFactory.getLogger(Server.class);

	private Server() {

		System.out.println("╔═══╗                     ╔╗ ");
		System.out.println("║╔═╗║                    ╔╝╚╗");
		System.out.println("║║ ╚╝╔══╗╔══╗╔══╗╔══╗╔╗╔╗╚╗╔╝");
		System.out.println("║║ ╔╗║╔╗║║╔═╝║╔╗║║╔╗║║║║║ ║║ ");
		System.out.println("║╚═╝║║╚╝║║╚═╗║╚╝║║║║║║╚╝║ ║╚╗");
		System.out.println("╚═══╝╚══╝╚══╝╚══╝╚╝╚╝╚══╝ ╚═╝");
		System.out.println();

		logger.info("Starting Coconut ...");

		try {
			ServerSocketManager.openServerSocket();
			ServerSocketManager.listenForConnections();
		} catch (Exception e) {
			logger.error("Server Error", e);
		} finally {
			ServerSocketManager.closeServerSocket();
		}

	}

	public static void start() {
		new Server();
	}
}
