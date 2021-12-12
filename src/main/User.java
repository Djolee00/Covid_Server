package main;

public class User {

	private String username;
	private String password;
	private String name;
	private String surname;
	private String personalID;
	private String gender;
	private String email;
	private int firstDose;
	private int secondDose;
	private int thirdDose;

	
	
	public User(String username, String password, String name, String surname, String personalID, String gender,
			String email, int firstDose, int secondDose, int thirdDose) {
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.personalID = personalID;
		this.gender = gender;
		this.email = email;
		this.firstDose = firstDose;
		this.secondDose = secondDose;
		this.thirdDose = thirdDose;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPersonalID() {
		return personalID;
	}

	public void setPersonalID(String personalID) {
		this.personalID = personalID;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", name=" + name + ", surname=" + surname
				+ ", personalID=" + personalID + ", gender=" + gender + ", email=" + email + ", firstDose=" + firstDose
				+ ", secondDose=" + secondDose + ", thirdDose=" + thirdDose + "]";
	}

	public int getFirstDose() {
		return firstDose;
	}

	public void setFirstDose(int firstDose) {
		this.firstDose = firstDose;
	}

	public int getSecondDose() {
		return secondDose;
	}

	public void setSecondDose(int secondDose) {
		this.secondDose = secondDose;
	}

	public int getThirdDose() {
		return thirdDose;
	}

	public void setThirdDose(int thirdDose) {
		this.thirdDose = thirdDose;
	}

	public String getUsername() {
		return username;
	}


}
