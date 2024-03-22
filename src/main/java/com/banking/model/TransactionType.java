package com.banking.model;

public enum TransactionType {
	WITHDRAW(1), DEPOSIT(2);

	private final int value;

	TransactionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static TransactionType fromValue(int value) {
		for (TransactionType type : TransactionType.values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid TransactionType value: " + value);
	}
}
