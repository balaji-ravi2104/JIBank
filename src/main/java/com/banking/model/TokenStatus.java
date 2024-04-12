package com.banking.model;

public enum TokenStatus {
	ACTIVE(1), EXPIRED(2);

	private final int value;

	private TokenStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static TokenStatus fromValue(int value) {
		for (TokenStatus TokenStatus : TokenStatus.values()) {
			if (TokenStatus.value == value) {
				return TokenStatus;
			}
		}
		throw new IllegalArgumentException("Invalid AccountTokenStatus value: " + value);
	}

	public static TokenStatus fromString(String value) {
		for (TokenStatus TokenStatus : TokenStatus.values()) {
			if (TokenStatus.name().equalsIgnoreCase(value)) {
				return TokenStatus;
			}
		}
		throw new IllegalArgumentException("Invalid AccountTokenStatus string: " + value);
	}
}
