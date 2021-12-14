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
		
		if(dbConnection == null) {
			clientOutput.println("Error while connecting to database");
			return;
		}
		
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
	 	
}
