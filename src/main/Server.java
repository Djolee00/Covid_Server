package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Server {

	public static void main(String[] args) {
		
		String jdbcURL = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11456529";
		String username = "sql11456529";
		String password = "sfrh9mlXtW";
		
//		String jdbcURL = "jdbc:mysql://localhost:3306/baza";
//		String username = "root";
//		String password = "pluralsight";
		
		int port = 3333;
		ServerSocket welcomeSocket=null;
		Socket communicationSocket = null;
		
		try {
		
			dbConnection = DriverManager.getConnection(jdbcURL, username, password);
			System.out.println("Database successfully connected");
			
			welcomeSocket = new ServerSocket(port);
			
			while(true) {
				System.out.println("Waiting for connection...");
				communicationSocket = welcomeSocket.accept();
				System.out.println("Connection with client established\n");
				
				
				new ServerThread(communicationSocket, dbConnection).start();
				
				
			}
			
		} catch (SQLException e) {
			System.out.println("Error while connecting with database...");
		} catch (IOException e) {
			System.out.println("Error while starting server...");
		}
		
	}

}
