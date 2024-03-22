package com.banking.model;

public enum AccountStatus {
	ACTIVE(1), INACTIVE(2);

	private final int value;

	private AccountStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AccountStatus fromValue(int value) {
		for (AccountStatus status : AccountStatus.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid AccountStatus value: " + value);
	}
}
