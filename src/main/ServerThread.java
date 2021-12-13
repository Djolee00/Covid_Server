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

		clientOutput.println("***\nMAIN MENU***" 
				+ "\n\n1.Register"
				+ "\n2.Login" 
				+ "\n3.Login as Administrator"
				+ "\n\nTo exit application at any time, please type:" 
				+ "\n***exit" 
				+ "\n\nTo get back to main menu at any time, please type:" 
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

		clientOutput.println("\n\n***WELCOME, PLEASE FILL REGISTRATION FORM***");

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
		
		userProfileMenu();
		
	}
	
	//***USER PROFILE***

	private void userProfileMenu() {
		
		clientOutput.println("\n\n***WELCOME***\n");
		clientOutput.println("Your personal data:");
		String format = "%-10s %-15s %-20s %-10s %-35s%-1s";
		String ulepsavanje="";
		for(int i =1;i<=94;i++) {
			ulepsavanje+="=";
		}
		clientOutput.println(ulepsavanje+"\n"
				+String.format(format, "Name:","Surname:","Personal ID:","Gender:","Email:","|")+"\n"+
				String.format(format, "----","-------","-----------","------","-----","|")+"\n"+
				String.format(format, user.getName(),user.getSurname(),user.getPersonalID(),user.getGender(),user.getEmail(),"|")+"\n"+
				ulepsavanje);
		
		String format2 = "%-25s%-25s%-25s%-1s";
		String ulepsavanje2="";
		for(int i=1;i<=75;i++) {
			ulepsavanje2+="=";
		}
		clientOutput.println(String.format(format2, "First Dose:","Second Dose:","Third Dose:","|")+"\n"
				+String.format(format2, "----------","-----------","----------","|")+
				"\n"+String.format(format2,getVaccine(user.getFirstDose()),getVaccine(user.getSecondDose()),getVaccine(user.getThirdDose()),"|")+"\n"+
				ulepsavanje2);
	
		
		String choice = null;
		boolean isValid = false;
		clientOutput.println("1.Change my answers\n"
				+ "2.Check my green certificate status\n"
				+ "3.Log out\n"
				+"Choose:");
		
		try {
			do {
			if(communicationSocket!=null && !communicationSocket.isClosed())
				choice = clientInput.readLine();
			
			if(isExit(choice)) {
				return;
			}
			
			if(isReturn(choice)) {
				clientOutput.println("\n\n");
				user = null;
				menu();
				return;
			}
			
			if(choice!=null)
				if(choice.equals("1") || choice.equals("2") || choice.equals("3")){
					isValid = true;
				}else {
					clientOutput.println("Invalid input try again");
				}
			}while(!isValid);
			
		} catch (IOException e) {
			clientOutput.println("Error while getting client input");
		}
		
		if(choice!=null) {
			switch (choice) {
				case "1":
					answerChangeMenu();
					userProfileMenu();
					break;
				case "2":
					checkGreenCertificate();
					userProfileMenu();
					break;
				case "3":
					user=null;
					menu();
					return;		
			}
		}
	
	}
	//***ANSWER CHANGE MENU***
	
	private void answerChangeMenu() {
		int choice = 0;
		String secondChoice = null;
		boolean valid = false;
		
		//FIRST QUESTION
		if(user.getFirstDose() == 0) {
			clientOutput.println("\n***FIRST DOSE***");
			
			choice = vaccineChoiceMenu();
			if(choice == -1) {
				return;
			}
			if(choice == -2) {
				clientOutput.println("\n\n");
				user = null;
				menu();
				return;
			}
			if(choice == 0) {
				return;
			}else {
				updateFirstDose(choice);
				user.setFirstDose(choice);
			}
		}
		
		//SECOND QUESTION
		if(user.getSecondDose() == 0) {
			clientOutput.println("\n***SECOND DOSE***");
			try {
			do {
				clientOutput.println("\nHave you been vaccinated with second dose?"
						+ "\n\n1.Yes\n2.No");
				clientOutput.println("Answer: ");
				secondChoice=clientInput.readLine();
				if(isExit(secondChoice)) {
					return;
				}
				if(isReturn(secondChoice)) {
					clientOutput.println("\n\n");
					user = null;
					menu();
					return;
				}
				
				if(secondChoice!=null)
					switch (secondChoice){
						case "1":
							valid=true;
							user.setSecondDose(user.getFirstDose());
							updateSecondDose();
							break;
						case "2":
							valid = true;
							break;
						default:
							clientOutput.println("Invalid input try again");
				}
			}while(!valid);
			}catch (IOException e) {
				clientOutput.println("Error while getting client input");
			}
		}
		
		choice = 0;
		//THIRD QUESTION
		if(user.getSecondDose()>0 && user.getThirdDose() == 0) {
			clientOutput.println("\n***THIRD DOSE***");
			
			choice = vaccineChoiceMenu();
			if(choice == -1) {
				return;
			}
			if(choice == -2) {
				clientOutput.println("\n\n");
				user = null;
				menu();
				return;
			}
			if(choice == 0) {
				return;
			}else {
				updateThirdDose(choice);
				user.setThirdDose(choice);
			}
		}else {
			clientOutput.println("\nAll questions have been answered");
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
	private void updateThirdDose(int choice) {
		String sql = "UPDATE korisnici SET trecaDoza=? WHERE username=?";
		try(PreparedStatement statement = dbConnection.prepareStatement(sql)){
			statement.setString(2, user.getUsername());
			statement.setInt(1, choice);
			
			int row=statement.executeUpdate();
			if(row>0) {
				System.out.println("Third dose successfully updated for user: "+user.getUsername());
			}else {
				clientOutput.println("Third dose status could not be updated");
			}
		}catch(SQLException e) {
			clientOutput.println("Error while updating first dose status");
		}
	}
	
	private void updateSecondDose() {
		String sql = "UPDATE korisnici SET drugaDoza=? WHERE username=?";
		try(PreparedStatement statement = dbConnection.prepareStatement(sql)){
			statement.setString(2, user.getUsername());
			statement.setInt(1, user.getSecondDose());
			
			int row=statement.executeUpdate();
			if(row>0) {
				System.out.println("Second dose successfully updated for user: "+user.getUsername());
			}else {
				clientOutput.println("Second dose status could not be updated");
			}
		}catch(SQLException e) {
			clientOutput.println("Error while updating first dose status");
		}
		
	}
	
	private void updateFirstDose(int choice) {
		String sql = "UPDATE korisnici SET prvaDoza=? WHERE username=?";
		try(PreparedStatement statement = dbConnection.prepareStatement(sql)){
			statement.setString(2, user.getUsername());
			statement.setInt(1, choice);
			
			int row=statement.executeUpdate();
			if(row>0) {
				System.out.println("First dose successfully updated for user: "+user.getUsername());
			}else {
				clientOutput.println("First dose status could not be updated");
			}
		}catch(SQLException e) {
			clientOutput.println("Error while updating first dose status");
		}
		
	}
	
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
	
	private void checkGreenCertificate() {
		if(user.getSecondDose() > 0) {
			clientOutput.println("\nYou possess valid green certificate");
		}else {
			clientOutput.println("\nYou don't possess valid green cerficate, because you didn't get second dose");
		}
	}

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
	
	//***GET VACCINE***
	
	private String getVaccine(int num) {
		switch(num) {
		case 1:
			return "Pfizer-BioNTech";
		case 2:
			return "Sputnik V";
		case 3:
			return "Sinopharm";
		case 4: 
			return "AstraZeneca";
		default:
			return "Vaccine not received";
		}
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
