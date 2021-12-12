package main;

import java.awt.Choice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
				+ "\n2.Login" 
				+ "\n3.Login as Administrator"
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
				logInMenu();
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
		int firstDose =0;
		int secondDose = 0;
		int thirdDose = 0;

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
				
				if(username.equals("") || username.contains(" ")) {
					clientOutput.println("Invalid input! Try again");
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
				
				if(password.length()>=5) {
					valid = true;
				}else {
					clientOutput.println("Invalid input. Try again");
				}
				
			}while(!valid);
			
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
					String[] nameSurname = s.split(" ",2);
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
			
			clientOutput.println("***\n\nFIRST DOSE***");
			firstDose=vaccineChoiceMenu();
	
			if(firstDose == -1) {
				return;
			}
			
			if(firstDose == -2) {
				clientOutput.println("\n\n");
				menu();
				return;
			}

			valid =false;
			if(firstDose > 0) {
				clientOutput.println("***SECOND DOSE***\n");
				clientOutput.println("Have you been vaccinated with second dose?"
						+ "\n\n1.Yes\n2.No");
				String choice;
				do {
					clientOutput.println("Answer: ");
					choice=clientInput.readLine();
					if(isExit(choice)) {
						return;
					}
					if(isReturn(choice)) {
						clientOutput.println("\n\n");
						menu();
						return;
					}
					
					if(choice!=null)
						switch (choice){
							case "1":
								valid=true;
								secondDose = firstDose;
								break;
							case "2":
								valid = true;
								break;
							default:
								clientOutput.println("Invalid input try again");
					}
				}while(!valid);
			}
			
			if(secondDose>0) {
				clientOutput.println("***THIRD DOSE***\n");
				thirdDose = vaccineChoiceMenu();
				if(thirdDose == -1) {
					return;
				}
				
				if(thirdDose == -2) {
					clientOutput.println("\n\n");
					menu();
					return;
				}
			}
			
			addUserInDatabase(username,password,name,surname,personalID,gender,email,firstDose,secondDose,thirdDose);
			menu();
		} catch (IOException ex) {
			clientOutput.println("Error while getting client input");
		}
	}
	
	//***LOGIN MENU***
	
	private void logInMenu() {
		String username;
		String password;
		boolean valid = false;
		
		clientOutput.println("\n\n***LOGIN FORM***\n");
		
		try {
			do {
			clientOutput.println("Username: ");
			username = clientInput.readLine();
			
			if(isExit(username)) {
				return;
			}
			if(isReturn(username)) {
				clientOutput.println("\n\n");
				menu();
				return;
			}
			
			clientOutput.println("Password: ");
			password = clientInput.readLine();
			
			if(isExit(password)) {
				return;
			}
			if(isReturn(password)) {
				clientOutput.println("\n\n");
				menu();
				return;
			}
			
			getBackUserFromDatabase(username,password);
			
			if(user != null) {
				valid = true;
			}else {
				clientOutput.println("Invalid username or password. Try again!");
			}
			
			}while(!valid);
		} catch (IOException e) {
			clientOutput.println("Error while getting client input.");
		}
		
		
	}
	
	//***VACCINE CHOICE MENU***

	private int vaccineChoiceMenu() {
		clientOutput.println("\n1.Pfizer-BioNTech"
				+ "\n2.Sputnik V"
				+ "\n3.Sinopharm"
				+ "\n4.AstraZeneca"
				+ "\n0.Vaccine not received\n\n");
		
		String choice;
		do {
			clientOutput.println("Answer: ");
			
			try {
				choice = clientInput.readLine();
				
				if(isExit(choice)) {
					return -1;
				}
				
				if(isReturn(choice)) {
					return -2;
				}
				
				if(choice!=null) {
					switch(choice) {
					case "1":
						return 1;
					case "2":
						return 2;
					case "3":
						return 3;
					case "4":
						return 4;
					case "0":
						return 0;
					default:
						clientOutput.println("Invalid choice. Try again!");
						break;				
					}
				}
			} catch (IOException e) {
				clientOutput.println("Error while getting client input!");
			}
	
		}while(true);
		
	}


	//***WORK WITH DATABASE***
	
	private void getBackUserFromDatabase(String username, String password) {
		String query = "SELECT * FROM korisnici WHERE username ='"+username+"' AND password ='"+password+"'";
		try (PreparedStatement statement = dbConnection.prepareStatement(query)){
			
			ResultSet result = statement.executeQuery(query); 
			
			if(result.next()) {
				user = new User(result.getString(1), result.getString(2), result.getString(3), 
						result.getString(4), result.getString(5),result.getString(6), result.getString(7),result.getInt(8), result.getInt(9),result.getInt(10));
			}
			
		} catch (SQLException e) {
			clientOutput.println("Error while searching database");
			e.printStackTrace();
		}
	}
	
	private void addUserInDatabase(String username, String password, String name, String surname, String personalID,
			String gender, String email, int firstDose, int secondDose, int thirdDose) {
		String sqlInsert = "INSERT INTO korisnici (username,password,ime,prezime,JMBG,pol,email,prvaDoza,drugaDoza,trecaDoza) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = dbConnection.prepareStatement(sqlInsert)){
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, name);
			statement.setString(4, surname);
			statement.setString(5, personalID);
			statement.setString(6, gender);
			statement.setString(7, email);
			statement.setInt(8, firstDose);
			statement.setInt(9, secondDose);
			statement.setInt(10, thirdDose);
			
			int rowCount = statement.executeUpdate();
			
			if(rowCount>0) {
				System.out.println("New user: "+username+" has been successfully added to database");
				clientOutput.println("Registration completed!");
			}else {
				System.out.println("New user could not be added");
				clientOutput.println("Sorry, registraion failed");
			}
			
		} catch (SQLException e) {
			clientOutput.println("Error while adding new user to database");
		}		
	}
	
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
