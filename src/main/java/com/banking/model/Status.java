package com.banking.model;

public enum Status {
	SUCCESS(1), FAILURE(2);

	private final int value;

	private Status(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Status fromValue(int value) {
		for (Status status : Status.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid AccountStatus value: " + value);
	}

	public static Status fromString(String value) {
		for (Status status : Status.values()) {
			if (status.name().equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid AccountStatus string: " + value);
	}
}
