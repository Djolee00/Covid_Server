package main;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrator {
	
	Connection dbConnection;
	PrintStream clientOutput;

	public Administrator(Connection dbConnection, PrintStream clientOutput) {
		this.dbConnection = dbConnection;
		this.clientOutput = clientOutput;
	}
	
	public void doesUserPossessValidCertificate(String personalID) {
		
		String sql = "SELECT ime, drugaDoza FROM korisnici WHERE JMBG=?";
		
		
		try(PreparedStatement statement = dbConnection.prepareStatement(sql)){
			statement.setString(1, personalID);
			
			ResultSet row = statement.executeQuery();
			if(row.next()) {
				if(row.getInt(2) > 0) {
					clientOutput.println("User: "+row.getString(1)+" possesses valid green certficate");
				}else {
					clientOutput.println("User: "+row.getString(1)+" doesn't possess valid green certficate");
				}
				
			}else {
				clientOutput.println("User with personal ID: "+personalID+" doesn't exist. Try again");
			}
			
		} catch (SQLException e) {
			clientOutput.println("Error while searching database");
			e.printStackTrace();
		}
	}
	
	public String getListOfUsers() throws SQLException {
		String sql = "SELECT * FROM korisnici";
		int counter = 0;
		String format = "%-10s%-15s%-20s%-10s%-35s%-15s%-1s";
		String listOfUsers = String.format(format, "Name:","Surname:","Personal ID:","Gender:","Email:","Vac.Status:","*")+"\n"+
				String.format(format, "----","-------","-----------","------","-----","----------","*")+"\n";
	
		
		
		PreparedStatement statement = dbConnection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			counter++;
			listOfUsers+=String.format(format, result.getString(3), result.getString(4),result.getString(5),result.getString(6),result.getString(7),getNumberOfDoses(result),"*")+"\n";
		}
		
		if(counter>0)
			return listOfUsers;
		else
			return null;
	}

	public String getNumbersPerDose() throws SQLException {
		String sql = "SELECT prvaDoza, drugaDoza, trecaDoza FROM korisnici";
		
		PreparedStatement statement = dbConnection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		int first = 0;
		int second = 0;
		int third = 0;
		
		while(result.next()) {
			if(result.getInt(3) > 0)
				third++;
			else if(result.getInt(2)>0)
				second++;
			else if(result.getInt(1)>0)
				first++;
		}
		
		String format = "%-15s%-15s%-15s";
		String stat = String.format(format, "First dose:","Second dose:","Third dose:")+"\n"+
				String.format(format, "----------","-----------","---------")+
				"\n"+String.format(format, String.valueOf(first),String.valueOf(second),String.valueOf(third));
		return stat;
	}

	public String getNumbersPerManufacturer() throws SQLException {
		String sql = "SELECT drugaDoza FROM korisnici";
		int pfizer = 0;
		int astra=0;
		int sino = 0;
		int sputn = 0;
		
		PreparedStatement statement = dbConnection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			switch (result.getInt(1)) {
				case 1:
					pfizer++;
					break;
				case 2:
					sputn++;
					break;
				case 3:
					sino++;
					break;
				case 4:
					astra++;
					break;
					
			}
		}
		
		String format = "%-20s%-13s%-13s%-13s%-1s";
		String ulepsavanje="*";
		for(int i=1;i<=59;i++) {
			ulepsavanje+="*";
		}
		
		String statistic =ulepsavanje+"\n" +String.format(format, "Pfizer-BioNTech:","Sputnik V:","Sinopharm:","AstraZeneca:","*")+"\n"
				+String.format(format, "---------------","---------","---------","-----------","*")+"\n"+
				String.format(format, String.valueOf(pfizer),String.valueOf(sputn),String.valueOf(sino),String.valueOf(astra),"*")+"\n"+
				ulepsavanje;
		
		return statistic;
		
	}
	
	private String getNumberOfDoses(ResultSet result) throws SQLException {

		int prvaD = result.getInt(8);
		int drugaD = result.getInt(9);
		int trecaD = result.getInt(10);
		
		if(trecaD >0)
			return "3";
		if(drugaD>0)
			return "2";
		if(prvaD>0)
			return "1";
		
		return "0";
	}

	
}