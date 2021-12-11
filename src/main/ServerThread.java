package main;

import java.awt.Choice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerThread extends Thread {

	// ***STREAMS & SOCKET***

	private BufferedReader clientInput = null;
	private PrintStream clientOutput = null;
	private Socket communicationSocket = null;
	private Connection dbConnection = null;

	private User user = null;

	// ***CONSTRUCTOR***

	public ServerThread(Socket communicationSocket, Connection dbConnection) {
		this.communicationSocket = communicationSocket;
		this.dbConnection = dbConnection;
	}

	// ***MAIN MENU***

	private void menu() {

		String clientChoice = null;

		clientOutput.println("***MAIN MENU***" 
				+ "\n\n1. Register"
				+ "\n2. Login" 
				+ "\n3. Login as Administrator"
				+ "\n\nTo exit application at eny time, please type:" 
				+ "\n***exit" 
				+ "\n\nTo get back to main menu, please type:" 
				+ "\n***return"
				+ "\n\nYour choice?  ");

		try {
			if (!communicationSocket.isClosed()) {
				clientChoice = clientInput.readLine();
			}

			if (isExit(clientChoice)) {
				return;
			}
			
			if(isReturn(clientChoice)) {
				clientOutput.println("\n");
				menu();
				return;
			}

		} catch (IOException e) {
			System.out.println("In Main Menu client input error!");
		}

		if (clientChoice != null)
			switch (clientChoice) {
			case "1":
				registerMenu();
				break;
			case "2":

				break;
			case "3":

				break;
			default:
				clientOutput.println("***Please enter valid input. Try again!***\n\n");
				menu();
				break;
			}
	}

	// ***REGISTER MENU***
	private void registerMenu() {
		String username;
		String password;
		String name=null;
		String surname=null;
		String personalID;
		String gender;
		String email;
		boolean valid = false;

		clientOutput.println("\n\n***WELCOME, PLEASE FILL REGISTRAION FORM***");

		try {
			do {
				clientOutput.println("Enter username(without spaces): ");
				username = clientInput.readLine();

				if (isExit(username)) {
					return;
				}
				
				if(isReturn(username)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
				if(username.contains(" ")) {
					clientOutput.println("Please enter username withoud spaces! Try again");
					continue;
				}

				if(!isUsernameUnique(username)) {
					clientOutput.println("Username already exist. Try again");
				}else {
					valid = true;
				}

			} while (!valid);
			
			valid = false;
			
			do {
				clientOutput.println("Enter password (minimum length is 5): ");
				password = clientInput.readLine();

				if (isExit(password)) {
					return;
				}
				
				if(isReturn(password)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
			}while(password.length()<5);
			
			valid = false;
			
			do {
				clientOutput.println("Enter name and surname (space between): ");
				String s = clientInput.readLine();

				if (isExit(s)) {
					return;
				}
				
				if(isReturn(s)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
				if(!s.contains(" ")) {
					clientOutput.println("Invalid input, try again!");
				}else {
					valid = true;
					String[] nameSurname = s.split(" ");
					name = nameSurname[0];
					surname = nameSurname[1];
				}
				
			}while(!valid);
			
			valid = false;
			
			do {
				clientOutput.println("Enter your perosnal ID (13 digits): ");
				personalID = clientInput.readLine();

				if (isExit(personalID)) {
					return;
				}
				
				if(isReturn(personalID)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
				if(personalID.length()!=13 || !personalID.matches("[0-9]+")) {
					clientOutput.println("Invalid input! Try again");
					continue;
				}
				
				if(!isPersonalIdUnique(personalID)) {
					clientOutput.println("Personal ID must be unique! Try again");
					continue;
				}else {
					valid = true;
				}
				
			}while(!valid);
			
			valid = false;
			
			do {
				clientOutput.println("Enter your gender (M/F): ");
				gender = clientInput.readLine();

				if (isExit(gender)) {
					return;
				}
				
				if(isReturn(gender)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
				if(gender.equals("M") || gender.equals("F")) {
					valid = true;
				}else {
					clientOutput.println("Invalid input! Try again");
				}
				
			}while(!valid);
			
			
			valid = false;
			
			do {
				clientOutput.println("Enter your email: ");
				email = clientInput.readLine();

				if (isExit(email)) {
					return;
				}
				
				if(isReturn(email)) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
				
				if(email.contains(" ") || !email.contains("@")) {
					clientOutput.println("Invalid input. Try again");
				}else {
					valid = true;
				}
				
			}while(!valid);
			
			User user= new User(username, password, name, surname, personalID, gender, email);
			System.out.println(user);
			
		} catch (IOException ex) {
			clientOutput.println("Error while getting client input");
		}
	}
	

	//***WORK WITH DATABASE***
	private boolean isUsernameUnique(String username) {
		boolean unique = true;
		
		String sqlQuery = "SELECT username FROM korisnici";
		
		try(Statement statement = dbConnection.createStatement();
				ResultSet result = statement.executeQuery(sqlQuery)){
			
			while(result.next()) {
				String userFromDb = result.getString(1); //1 column index in result set,starts from 1
				if(userFromDb.equals(username)) {
					unique = false;
					break;
				}
			}
		}catch (SQLException e) {
			System.out.println("DB Error - username search");
		}
		
		return unique;
	}
	
	private boolean isPersonalIdUnique(String personalID) {
		boolean unique = true;
		
		String sqlQuery = "SELECT JMBG FROM korisnici";
		
		try(Statement statement = dbConnection.createStatement();
				ResultSet result = statement.executeQuery(sqlQuery)){
			
			while(result.next()) {
				String personalIDFromDb = result.getString(1); //1 column index in result set,starts from 1
				if(personalIDFromDb.equals(personalID)) {
					unique = false;
					break;
				}
			}
		}catch (SQLException e) {
			System.out.println("DB Error - personal ID search");
		}
		
		return unique;
	}
	

	// ***CHECK Methods***

	private boolean isExit(String s) {
		if (s == null || s.startsWith("***exit")) {
			closeCommunication();
			return true;
		}
		return false;
	}
	
	private boolean isReturn(String s) {
		return s == null || s.startsWith("***return");
	}
	

	// ***CLOSING COMMUNICATION***
	private void closeCommunication() {
		try {
			clientOutput.println(">> Goodbye");
			communicationSocket.close();
		} catch (IOException e) {
			System.out.println("Error while closing communication socket with client");
		}
	}

	// ***MAIN (RUN) METHOD***
	@Override
	public void run() {
		try {
			clientInput = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());

			menu();

		} catch (IOException e) {
			System.out.println("Error with I/O streams...");
		}
	}

}
