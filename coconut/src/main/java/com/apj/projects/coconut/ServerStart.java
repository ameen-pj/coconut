package com.apj.projects.coconut;

import com.apj.projects.coconut.concurent.ThreadpoolService;
import com.apj.projects.coconut.server.Server;

public class ServerStart {
	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ThreadpoolService.shutdown();
			}
		});

		Server.start();

	}
}
