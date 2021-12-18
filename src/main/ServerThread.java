package main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class ServerThread extends Thread {

	// ***STREAMS & SOCKET***

	private BufferedReader clientInput = null;
	private PrintStream clientOutput = null;
	private Socket communicationSocket = null;
	private Connection dbConnection = null;

	private User user = null;
	private Administrator admin = null;

	// ***CONSTRUCTOR***

	public ServerThread(Socket communicationSocket, Connection dbConnection) {
		this.communicationSocket = communicationSocket;
		this.dbConnection = dbConnection;
	}

	// ***MAIN MENU***

	private void menu() {

		String clientChoice = null;

		clientOutput.println("\n***MAIN MENU***" 
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
			System.err.println("Error while getting client input in Main Menu");
			closeCommunication();
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
				logInAsAdmin();
				break;
			default:
				clientOutput.println("***Please enter valid input. Try again!***\n\n");
				menu();
				break;
			}
	}

	//***LOGIN AS ADMIN
	
	private void logInAsAdmin() {
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
			
			
			if(username.equals("admin") && password.equals("admin")) {
				valid = true;
				admin = new Administrator(dbConnection, clientOutput);
			}else {
				clientOutput.println("Invalid username or password. Try again!");
			}
			
			}while(!valid);
			
			adminMenu();
		} catch (IOException e) {
			System.err.println("Error while getting client input in Admin Login Menu");
			closeCommunication();
		}
		
		
	}
	
	//***ADMIN MENU***
	private void adminMenu() {
		String choice = null;
		boolean valid = false;
		
		clientOutput.println("\n\n***WELCOME ADMIN***");
		
		clientOutput.println("Choose one option:");
		clientOutput.println("1.Check if user possesses green certificate\n2.List of users"
				+ "\n3.Statistic of vaccination per dose\n4.2+ doses statistic per vaccine manufacturer");
		clientOutput.println("5.Log out");
		
		try {
			do {
			clientOutput.println("Answer: ");
			if(communicationSocket!=null && !communicationSocket.isClosed())
				choice = clientInput.readLine();
			if(isExit(choice)) {
				return;
			}
			if(isReturn(choice)) {
				admin = null;
				clientOutput.println("\n");
				menu();
				return;
			}
			
			if(choice != null) {
				switch(choice) {
				case "1":
					checkUserGreenStatus();
					return;
				case "2":
					showListOfUsers();
					return;
				case "3":
					showVacStatus();
					return;
				case "4":
					showTwoPlusDosesStatistic();
					return;
				case "5":
					admin = null;
					menu();
					return;
				default:
					clientOutput.println("Invalid input try again!");
					break;
				}
			}
			
			}while(!valid);
			
		}catch(IOException ex) {
			System.err.println("Error while getting client input in Admin Menu");
			closeCommunication();
		}
		
	}

	private void showTwoPlusDosesStatistic() {
		try {
			clientOutput.println("2+ doses per manufacturer");
			String statistic = admin.getNumbersPerManufacturer();
			clientOutput.println(statistic);
			adminMenu();
			return;
		} catch (SQLException e) {
			clientOutput.println("Error while getting statistic for manufacturers");
		}
	}

	private void showVacStatus() {
		try {
			String stat = admin.getNumbersPerDose();
			
			clientOutput.println("=============================================================\nVaccinated with only one, two or three doses\n=============================================================");
			clientOutput.println(stat);
			clientOutput.println("=============================================================");
		} catch (SQLException e) {
			clientOutput.println("Error while getting number of users per dose");
		} finally {
			adminMenu();
		}
	}

	private void showListOfUsers() {
		try {
			String usersList = admin.getListOfUsers();
			
			if(usersList == null) {
				clientOutput.println("*** There are no users in our system ***");
				adminMenu();
				return;
			}
			
			String ulepsavanje="";
			for(int i=1;i<=106;i++) {
				ulepsavanje+="*";
			}
			clientOutput.println(ulepsavanje);
			clientOutput.println(usersList);
			clientOutput.println(ulepsavanje);
			
		
			
		} catch (SQLException e) {
			clientOutput.println("Error while getting list of users");
		}finally {
			adminMenu();
		}
	}

	private void checkUserGreenStatus() throws IOException {
		clientOutput.println("\nPlease enter user personal ID (type ***return to get back):");
		String choice;
		while(true) {
			clientOutput.println("Personal ID: ");
			choice =  clientInput.readLine();
			if(choice == null) {
				return;
			}
			if(isExit(choice)) {
				return;
			}
			if(choice.equals("***return")) {
				adminMenu();
				return;
			}
			
			admin.doesUserPossessValidCertificate(choice);
		}
		
	}

	
	// ***REGISTER MENU***
	private void registerMenu() {
		String username = null;
		String password=null;
		String name=null;
		String surname=null;
		String personalID=null;
		String gender=null;
		String email=null;
		boolean valid = false;
		int firstDose =0;
		int secondDose = 0;
		int thirdDose = 0;
		GregorianCalendar firstDate = null;
		GregorianCalendar secondDate = null;
		GregorianCalendar thirdDate = null;

		clientOutput.println("\n\n***WELCOME, PLEASE FILL REGISTRATION FORM***");

		try {
			do {
				clientOutput.println("Enter username (without spaces): ");
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
			
			clientOutput.println("\n\n***FIRST DOSE***");
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
				String choice;
				valid=false;
				firstDate = enterDate(1,firstDate,secondDate);
				if(firstDate == null)
					return;
				
				clientOutput.println("***SECOND DOSE***\n");
				clientOutput.println("Have you been vaccinated with second dose?"
						+ "\n\n1.Yes\n2.No");
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
								secondDate = enterDate(2,firstDate,secondDate);
								if(secondDate == null)
									return;
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
				
				if(thirdDose>0) {
					thirdDate = enterDate(3, firstDate, secondDate);
					if(thirdDate == null)
						return;
				}
			}
			
			addUserInDatabase(username,password,name,surname,personalID,gender,email,firstDose,secondDose,thirdDose,firstDate,secondDate,thirdDate);
			menu();
		} catch (IOException ex) {
			System.err.println("Error while getting client input in Register Menu");
			closeCommunication();
		}
	}
	
	//***ENTER DATE METHOD***
	private GregorianCalendar enterDate(int dose,GregorianCalendar first,GregorianCalendar second) throws IOException {
		String choice=null;
		boolean valid = false;
		GregorianCalendar dateOfDose = null;
		
		if(dose == 1) {
			do {
				clientOutput.println("Enter date of vaccination in 2021.(dd/MM/yyyy): ");
				choice = clientInput.readLine();

				if (isExit(choice)) {
					return null;
				}
			
				if(isReturn(choice)) {
					clientOutput.println("\n\n");
					menu();
					return null;
				}
			
				if(isDateValid(choice)) {
					valid = true;
				}else {
					clientOutput.println("Invalid date format. Try again!");
				}
			}while(!valid);
		}
		
		if(dose == 2) {
			do {
				clientOutput.println("Enter date of second vaccination in 2021.(dd/MM/yyyy): ");
				choice = clientInput.readLine();

				if (isExit(choice)) {
					return null;
				}
			
				if(isReturn(choice)) {
					clientOutput.println("\n\n");
					menu();
					return null;
				}
			
				if(!isDateValid(choice)) {
					clientOutput.println("Invalid date format. Try again!");
					continue;
				}
				
				dateOfDose = giveDateOfVaccination(choice);
				
				if(isThreeWeekAfter(first,dateOfDose)) {
					break;
				}else {
					clientOutput.println("Second dose must be at least 3 weeks after first. Try again!");
					dateOfDose=null;
					continue;
				}
			}while(true);
		}
		
		if(dose == 3) {
			do {
				clientOutput.println("Enter date of third vaccination in 2021.(dd/MM/yyyy): ");
				choice = clientInput.readLine();

				if (isExit(choice)) {
					return null;
				}
			
				if(isReturn(choice)) {
					clientOutput.println("\n\n");
					menu();
					return null;
				}
			
				if(!isDateValid(choice)) {
					clientOutput.println("Invalid date format. Try again!");
					continue;
				}
				
				dateOfDose = giveDateOfVaccination(choice);
				if(isSixMonthsAfter(second,dateOfDose)) {
					break;
				}else {
					clientOutput.println("Third dose must be at least 6 months after second. Try again!");
					dateOfDose = null;
					continue;
				}
			}while(true);
		}
		
		return giveDateOfVaccination(choice);
	}
	

	//***METHOD TO CONVERT STRING TO VACC. DATE***
	private GregorianCalendar giveDateOfVaccination(String choice) {
		String [] s = choice.split("/");
		int day = Integer.parseInt(s[0]);
		int month = Integer.parseInt(s[1])-1;
		int year = Integer.parseInt(s[2]);
		return  new GregorianCalendar(year, month, day);
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
			
			userProfileMenu();
		} catch (IOException e) {
			System.err.println("Error while getting client input in Login menu");
			closeCommunication();
		}
		
		
		
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
			System.err.println("Error while getting client input in User Profile");
			closeCommunication();
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
				System.err.println("Error while getting client input in Answer Change Menu");
				closeCommunication();
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
		}
		if(user.getThirdDose()>0) {
			clientOutput.println("\n============================\nAll questions have been answered\n============================");
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
		try{
			do {
			clientOutput.println("Answer: ");
			
			
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
			}while(true);
		} catch (IOException e) {
			System.err.println("Error while getting client input in Vaccine Choice Menu");
			closeCommunication();
			return -1;
		}
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
			closeCommunication();
		}
	}
	
	private void addUserInDatabase(String username, String password, String name, String surname, String personalID,
			String gender, String email, int firstDose, int secondDose, int thirdDose,GregorianCalendar firstDate,GregorianCalendar secondDate
			,GregorianCalendar thirdDate) {
		String sqlInsert = "INSERT INTO korisnici (username,password,ime,prezime,JMBG,pol,email,prvaDoza,drugaDoza,trecaDoza,prvaDatum,drugaDatum,trecaDatum) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
		
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
			
			if(firstDate == null) {
				statement.setNull(11, java.sql.Types.DATE);
				statement.setNull(12, java.sql.Types.DATE);
				statement.setNull(13, java.sql.Types.DATE);
			}else {
				statement.setDate(11, new Date(firstDate.getTimeInMillis()));
				
				if(secondDate == null) {
					statement.setNull(12, java.sql.Types.DATE);
					statement.setNull(13, java.sql.Types.DATE);
				}else {
					statement.setDate(12, new Date(secondDate.getTimeInMillis()));
					
					if(thirdDate == null) {
						statement.setNull(13, java.sql.Types.DATE);
					}else {
						statement.setDate(13, new Date(thirdDate.getTimeInMillis()));
					}
				}
			}
			
			
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
			closeCommunication();
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
			System.err.println("DB Error - personal ID search");
			closeCommunication();
		}
		
		return unique;
	}
	

	// ***CHECK Methods***
	
	private boolean isDateValid(String s) {
		DateFormat checker = new  SimpleDateFormat("dd/MM/yyyy");
		checker.setLenient(false);
		try {
			checker.parse(s);
			String[] splitS = s.split("/");
			if(Integer.parseInt(splitS[2]) != 2021)
				return false;
		}catch(ParseException e) {
			return false;
		}
		
		return true;
	}
	
	private void checkGreenCertificate() {
		if(user.getSecondDose() > 0) {
			clientOutput.println("*********************************************\n"
					+ "You possess valid green certificate\n********************************************");
		}else {
			clientOutput.println(""
					+ "**************************************************************************\n"
					+ "You don't possess valid green cerficate, because you didn't get second dose\n"
					+ "**************************************************************************");
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
	
	private boolean isThreeWeekAfter(GregorianCalendar first, GregorianCalendar dateOfDose) {

		GregorianCalendar var = new GregorianCalendar(first.get(GregorianCalendar.YEAR),first.get(GregorianCalendar.MONTH),first.get(GregorianCalendar.DAY_OF_MONTH));
		var.add(GregorianCalendar.DAY_OF_MONTH, 21);
		
		return dateOfDose.after(var);
		
	}
	
	private boolean isSixMonthsAfter(GregorianCalendar second, GregorianCalendar dateOfDose) {
		GregorianCalendar var = new GregorianCalendar(second.get(GregorianCalendar.YEAR),second.get(GregorianCalendar.MONTH),second.get(GregorianCalendar.DAY_OF_MONTH));
		var.add(GregorianCalendar.MONTH, 6);
		return dateOfDose.after(var);
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
			System.err.println("Client has been disconnected");
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
