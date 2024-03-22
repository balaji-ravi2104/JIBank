package com.banking.model;

public enum AccountType {
	CURRENT(1), SAVINGS(2), SALARY(3), BUSINESS(4);

	private final int value;

	AccountType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AccountType fromValue(int value) {
		for (AccountType status : AccountType.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid AccountType value: " + value);
	}

}
