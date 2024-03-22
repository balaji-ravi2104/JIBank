package com.banking.model;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private int userId;
	private String password;
	private String firstName;
	private String lastName;
	private String gender;
	private String email;
	private String contactNumber;
	private String address;
	private long dateOfBirth;
	private UserType typeOfUser;
	private AccountStatus status;	

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(int status) {
		AccountStatus accountStatus = AccountStatus.fromValue(status);
		this.status = accountStatus;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public UserType getTypeOfUser() {
		return typeOfUser;
	}

	public void setTypeOfUser(int typeOfUser) {
		UserType type = UserType.fromValue(typeOfUser);
		this.typeOfUser = type;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", gender=" + gender + ", email=" + email + ", contactNumber=" + contactNumber
				+ ", address=" + address + ", dateOfBirth=" + dateOfBirth + ", typeOfUser=" + typeOfUser + ", Status="
				+ status + "]";
	}

}
