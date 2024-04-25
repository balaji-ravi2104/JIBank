package com.banking.utils;

import com.banking.logservice.AuditLogHandler;
import com.banking.model.Account;
import com.banking.model.AuditLog;
import com.banking.model.AuditlogActions;
import com.banking.model.Status;

public class AuditLogUtils {

	private static final AuditLogHandler auditLogHandler = new AuditLogHandler();

	public static void logUserCreation(int userId, int creatingUserId, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(userId);
		auditLog.setAuditlogActions(AuditlogActions.CREATE.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(creatingUserId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User id %d Created New User With Id of %d", creatingUserId, userId)
				: String.format("User id %d Try to Create New User but Failed", creatingUserId));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logUserUpdation(int userId, int updatingUserId, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(userId);
		auditLog.setAuditlogActions(AuditlogActions.UPDATE.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(updatingUserId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User id %d Updates the Details of User Id %d", updatingUserId, userId)
				: String.format("User id %d Try to Updates the Details of User Id %d But Failed", updatingUserId,
						userId));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logPasswordUpdation(int userId, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(userId);
		auditLog.setAuditlogActions(AuditlogActions.UPDATE.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(userId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(
				status == Status.SUCCESS ? String.format("User id %d Updated the Account login Password", userId)
						: String.format("User id %d Try to Updated the Account login Password but Failed", userId));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logAccountCreation(int userId, int creatingUserId, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(userId);
		auditLog.setAuditlogActions(AuditlogActions.CREATE.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(creatingUserId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Created the new Account for User Id %d ", creatingUserId, userId)
				: String.format("User Id %d Created the new Account for User Id %d but Failed", creatingUserId,
						userId));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logAccountStatusChange(int userId, String accountNumber, int updatingUserId, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(userId);
		auditLog.setAuditlogActions(AuditlogActions.UPDATE.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(updatingUserId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Updated the Account %s of User Id %d ", updatingUserId, accountNumber,
						userId)
				: String.format("User Id %d Updated the Account %s of User Id %d but Failed", updatingUserId,
						accountNumber, userId));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logAmountDeposit(int userId, Account account, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(account.getUserId());
		auditLog.setAuditlogActions(AuditlogActions.DEPOSIT.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(userId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Deposited Amount to the Account %s for User Id %d ", userId,
						account.getAccountNumber(), account.getUserId())
				: String.format("User Id %d Deposited Amount to the Account %s for User Id %d but Failed", userId,
						account.getAccountNumber(), account.getUserId()));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logAmountWithdraw(int userId, Account account, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(account.getUserId());
		auditLog.setAuditlogActions(AuditlogActions.WITHDRAW.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(userId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Withdraw Amount to the Account %s for User Id %d ", userId,
						account.getAccountNumber(), account.getUserId())
				: String.format("User Id %d Withdraw Amount to the Account %s for User Id %d but Failed", userId,
						account.getAccountNumber(), account.getUserId()));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logAmountTransfer(Account accountFromTransfer, String accountNumberToTransfer, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(accountFromTransfer.getUserId());
		auditLog.setAuditlogActions(AuditlogActions.TRANSFER.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(accountFromTransfer.getUserId());
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Transfer Amount from Account %s to Account %s ",
						accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
						accountNumberToTransfer)
				: String.format("User Id %d Transfer Amount from Account %s to Account %s but Failed",
						accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
						accountNumberToTransfer));
		auditLogHandler.addAuditData(auditLog);
	}

	public static void logTransactionView(int userId, int targetUserId, String accountNumber, Status status) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTargetId(targetUserId);
		auditLog.setAuditlogActions(AuditlogActions.VIEW.getValue());
		auditLog.setCreatedTime(System.currentTimeMillis());
		auditLog.setUserId(userId);
		auditLog.setStatus(status.getValue());
		auditLog.setDescription(status == Status.SUCCESS
				? String.format("User Id %d Viewed the Transaction of Account %s of User Id %d ", userId, accountNumber,
						targetUserId)
				: String.format("User Id %d Viewed the Transaction of Account %s of User Id %d but Failed", userId,
						accountNumber, targetUserId));
		auditLogHandler.addAuditData(auditLog);
	}

}
