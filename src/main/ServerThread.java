package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;

public class ServerThread extends Thread{
	
	private BufferedReader clientInput = null;
	private PrintStream clientOutput = null;
	private Socket communicationSocket = null;
	private Connection dbConnection = null;
	
	public ServerThread(Socket communicationSocket,Connection dbConnection) {
		this.communicationSocket = communicationSocket;
		this.dbConnection = dbConnection;
	}
	
	
	@Override
	public void run() {
		try {
			clientInput = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
		} catch (IOException e) {
			System.out.println("Error with I/O streams...");
		}
	}
	
}
