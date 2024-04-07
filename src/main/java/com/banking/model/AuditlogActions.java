package com.banking.model;

public enum AuditlogActions {
	LOGIN(1), LOGOUT(2), CREATE(3), UPDATE(4), DEPOSIT(5), WITHDRAW(6), TRANSFER(7),VIEW(8);

	private final int value;

	private AuditlogActions(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static AuditlogActions fromValue(int value) {
		for (AuditlogActions status : AuditlogActions.values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid AccountStatus value: " + value);
	}
	
	public static AuditlogActions fromString(String value) {
        for (AuditlogActions status : AuditlogActions.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AccountStatus string: " + value);
    }
}
