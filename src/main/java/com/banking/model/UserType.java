package com.banking.model;

public enum UserType {
	CUSTOMER(1), EMPLOYEE(2), ADMIN(3);

	private final int value;

	private UserType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static UserType fromValue(int value) {
		for (UserType userType : UserType.values()) {
			if (userType.value == value) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid UserType value: " + value);
	}

}
