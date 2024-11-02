package com.apj.projects.coconut.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.apj.projects.coconut.http.HTTPBody;
import com.apj.projects.coconut.http.HTTPRequest;
import com.apj.projects.coconut.http.HTTPRequestParser;
import com.apj.projects.coconut.http.HTTPResponse;
import com.apj.projects.coconut.http.HTTPResponseWriter;
import com.apj.projects.coconut.http.enums.HTTPStatusCode;

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

				HTTPRequest req = new HTTPRequestParser(socket.getInputStream()).parse();
				if (req != null) {

					HTTPResponse resp = new HTTPResponse();
					resp.setHTTPVersion("HTTP/1.1");
					resp.setStatus(HTTPStatusCode.OK);
					resp.setBody(new HTTPBody((new File(req.getHTTPURLPath().getParams("/{file}").get("file")))));
					HTTPResponseWriter writer = new HTTPResponseWriter(socket.getOutputStream(), resp);
					writer.buildResponse();
					writer.send();
				}

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
