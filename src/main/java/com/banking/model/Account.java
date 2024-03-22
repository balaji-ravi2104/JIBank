package com.banking.model;

import java.io.Serializable;

public class Account implements Serializable{
	private static final long serialVersionUID = 1L;
	private int accountId;
	private int userId;
	private String accountNumber;
	private AccountType accountType;
	private int branchId;
	private double balance;
	private AccountStatus accountStatus;
	private boolean primaryAccount;

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountTypeId) {
		AccountType accountType = AccountType.fromValue(accountTypeId);
		this.accountType = accountType;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(int accountStatusId) {
		AccountStatus accountStatus = AccountStatus.fromValue(accountStatusId);
		this.accountStatus = accountStatus;
	}

	public boolean isPrimaryAccount() {
		return primaryAccount;
	}

	public void setPrimaryAccount(boolean primaryAccount) {
		this.primaryAccount = primaryAccount;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", userId=" + userId + ", accountNumber=" + accountNumber
				+ ", accountType=" + accountType + ", branchId=" + branchId + ", balance=" + balance
				+ ", accountStatus=" + accountStatus + ", primaryAccount=" + primaryAccount + "]";
	}

}
