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
		Connection dbConnection = null;
		
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
				System.out.println("Connection with client established");
				
				
				new ServerThread(communicationSocket, dbConnection).start();
				
				
				PreparedStatement statement1=dbConnection.prepareStatement(
	                    "INSERT INTO korisnici " +
	                            "(username,password,ime,prezime,JMBG,pol,email) " +
	                            "VALUES ('djoka','djokacar','Djordje','Ivanovic','023232','M','232@sd')", Statement.RETURN_GENERATED_KEYS);
	            statement1.execute();
			}
			
		} catch (SQLException e) {
			System.out.println("Error while connecting with database...");
		} catch (IOException e) {
			System.out.println("Error while starting server...");
		}
		
	}

}
