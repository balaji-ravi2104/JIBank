package com.banking.model;

public enum TransactionStatus {
	SUCCESS(1), FAILURE(2);

	private final int value;

	TransactionStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static TransactionStatus fromValue(int value) {
		for (TransactionStatus status : TransactionStatus.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid TransactionStatus value: " + value);
	}
}
