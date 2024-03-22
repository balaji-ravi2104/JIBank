package com.banking.model;

public class Transaction {
	private int transactionId;
	private int userId;
	private long referenceId;
	private String viewerAccount;
	private String transactedAccount;
	private TransactionType transactionType;
	private double transactedAmount;
	private double balance;
	private long dateOfTransaction;
	private String remark;
	private TransactionStatus status;

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getViewerAccount() {
		return viewerAccount;
	}

	public void setViewerAccount(String viewerAccount) {
		this.viewerAccount = viewerAccount;
	}

	public String getTransactedAccount() {
		return transactedAccount;
	}

	public void setTransactedAccount(String transactedAccount) {
		this.transactedAccount = transactedAccount;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionTypeId) {
		TransactionType transactionType = TransactionType.fromValue(transactionTypeId);
		this.transactionType = transactionType;
	}

	public double getTransactedAmount() {
		return transactedAmount;
	}

	public void setTransactedAmount(double transactedAmount) {
		this.transactedAmount = transactedAmount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getDateOfTransaction() {
		return dateOfTransaction;
	}

	public void setDateOfTransaction(long dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(int status) {
		TransactionStatus transactionStatus = TransactionStatus.fromValue(status);
		this.status = transactionStatus;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", userId=" + userId + ", referenceId=" + referenceId
				+ ", viewerAccount=" + viewerAccount + ", transactedAccount=" + transactedAccount + ", transactionType="
				+ transactionType + ", transactedAmount=" + transactedAmount + ", balance=" + balance
				+ ", dateOfTransaction=" + dateOfTransaction + ", remark=" + remark + ", status=" + status + "]";
	}

}
