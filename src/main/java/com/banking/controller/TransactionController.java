package com.banking.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.AccountDao;
import com.banking.dao.TransactionDao;
import com.banking.dao.implementation.AccountDaoImplementation;
import com.banking.dao.implementation.TransactionDaoImplementation;
import com.banking.logservice.AuditLogHandler;
import com.banking.model.Account;
import com.banking.model.AuditLog;
import com.banking.model.AuditlogActions;
import com.banking.model.Status;
import com.banking.model.Transaction;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class TransactionController {

	private TransactionDao transactionDao;
	private AccountDao accountDao;
	private AuditLogHandler auditLogHandler;

	private static final Logger logger = LoggerProvider.getLogger(); 
	
	private static final Object accountCacheLock = new Object();
	private static final Object listOfAccountsLock = new Object();

	public TransactionController() {
		this.accountDao = new AccountDaoImplementation();
		this.transactionDao = new TransactionDaoImplementation();
		this.auditLogHandler = new AuditLogHandler();
	}

	public boolean depositAmount(Account account, double amountToDeposite, String description, int userId)
			throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isDepositeSuccess = false;

		synchronized (accountCacheLock) {
			AccountController.accountCache.rem(AccountController.accountCachePrefix + account.getAccountNumber());
		}
		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts.rem(AccountController.listAccountCachePrefix + account.getUserId());
		}

		try {
			isDepositeSuccess = transactionDao.deposit(account, amountToDeposite, description, userId);
			if (isDepositeSuccess) {
				AuditLog auditLog = new AuditLog(account.getUserId(), AuditlogActions.DEPOSIT,
						System.currentTimeMillis(), userId,
						String.format("User Id %d Deposited Amount to the Account %s for User Id %d ", userId,
								account.getAccountNumber(), account.getUserId()),
						Status.SUCCESS);
				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(account.getUserId(), AuditlogActions.DEPOSIT,
						System.currentTimeMillis(), userId,
						String.format("User Id %d Deposited Amount to the Account %s for User Id %d but Failed", userId,
								account.getAccountNumber(), account.getUserId()),
						Status.FAILURE);
				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Depositing Money",e);
			throw new CustomException("Exception Occured While Depositing Money", e);
		}
		return isDepositeSuccess;
	}

	public boolean withdrawAmount(Account account, double amountToWithdraw, String description, int userId)
			throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isWithdrawSuccess = false;

		synchronized (accountCacheLock) {
			AccountController.accountCache.rem(AccountController.accountCachePrefix + account.getAccountNumber());
		}
		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts.rem(AccountController.listAccountCachePrefix + account.getUserId());
		}

		try {
			isWithdrawSuccess = transactionDao.withdraw(account, amountToWithdraw, description, userId);
			if (isWithdrawSuccess) {
				AuditLog auditLog = new AuditLog(account.getUserId(), AuditlogActions.WITHDRAW,
						System.currentTimeMillis(), userId,
						String.format("User Id %d Withdraw Amount from the Account %s for User Id %d ", userId,
								account.getAccountNumber(), account.getUserId()),
						Status.SUCCESS);
				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(account.getUserId(), AuditlogActions.WITHDRAW,
						System.currentTimeMillis(), userId,
						String.format("User Id %d Withdraw Amount from the Account %s for User Id %d but Failed",
								userId, account.getAccountNumber(), account.getUserId()),
						Status.FAILURE);
				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Withdrawing Money",e);
			throw new CustomException("Exception Occured While Withdrawing Money", e);
		}
		return isWithdrawSuccess;
	}

	public boolean transferWithinBank(Account accountFromTransfer, Account accountToTransfer, double amountToTransfer,
			String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(remark, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isTransactionSuccess = false;

		synchronized (accountCacheLock) {
			AccountController.accountCache
					.rem(AccountController.accountCachePrefix + accountFromTransfer.getAccountNumber());
			AccountController.accountCache
					.rem(AccountController.accountCachePrefix + accountToTransfer.getAccountNumber());
		}

		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts
					.rem(AccountController.listAccountCachePrefix + accountFromTransfer.getUserId());
			AccountController.listOfAccounts
					.rem(AccountController.listAccountCachePrefix + accountToTransfer.getUserId());
		}

		try {
			isTransactionSuccess = transactionDao.transferMoneyWithinBank(accountFromTransfer, accountToTransfer,
					amountToTransfer, remark);
			if (isTransactionSuccess) {
				AuditLog auditLog = new AuditLog(accountFromTransfer.getUserId(), AuditlogActions.TRANSFER,
						System.currentTimeMillis(), accountFromTransfer.getUserId(),
						String.format("User Id %d Transfer Amount from Account %s to Account %s ",
								accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
								accountToTransfer.getAccountNumber()),
						Status.SUCCESS);
				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(accountFromTransfer.getUserId(), AuditlogActions.TRANSFER,
						System.currentTimeMillis(), accountFromTransfer.getUserId(),
						String.format("User Id %d Transfer Amount from Account %s to Account %s but Failed",
								accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
								accountToTransfer.getAccountNumber()),
						Status.FAILURE);
				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Transferring Money Within Bank",e);
			throw new CustomException("Exception Occured While Transferring Money Within Bank", e);
		}
		return isTransactionSuccess;
	}

	public boolean transferWithOtherBank(Account accountFromTransfer, String accountNumberToTransfer,
			double amountToTransferWithOtherBank, String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountNumberToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(remark, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isTransactionSuccess = false;

		synchronized (accountCacheLock) {
			AccountController.accountCache
					.rem(AccountController.accountCachePrefix + accountFromTransfer.getAccountNumber());
		}
		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts
					.rem(AccountController.listAccountCachePrefix + accountFromTransfer.getUserId());
		}
		try {
			isTransactionSuccess = transactionDao.transferMoneyWithOtherBank(accountFromTransfer,
					accountNumberToTransfer, amountToTransferWithOtherBank, remark);
			if (isTransactionSuccess) {
				AuditLog auditLog = new AuditLog(accountFromTransfer.getUserId(), AuditlogActions.TRANSFER,
						System.currentTimeMillis(), accountFromTransfer.getUserId(),
						String.format("User Id %d Transfer Amount from Account %s to Account %s ",
								accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
								accountNumberToTransfer),
						Status.SUCCESS);
				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(accountFromTransfer.getUserId(), AuditlogActions.TRANSFER,
						System.currentTimeMillis(), accountFromTransfer.getUserId(),
						String.format("User Id %d Transfer Amount from Account %s to Account %s but Failed",
								accountFromTransfer.getUserId(), accountFromTransfer.getAccountNumber(),
								accountNumberToTransfer),
						Status.FAILURE);
				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Transferring Money With Other Bank",e);
			throw new CustomException("Exception Occured While Transferring Money With Other Bank", e);
		}
		return isTransactionSuccess;
	}

	public List<Transaction> getTransactions(String accountNumber, String startDate, String endDate, int userId)
			throws CustomException {
		List<Transaction> transactions = null;
		try {
			transactions = transactionDao.getCustomerTransactions(accountNumber, startDate, endDate, userId);
			int targetUserId = accountDao.getAccountDetail(accountNumber).getUserId();
			if (transactions != null) {
				AuditLog auditLog = new AuditLog(targetUserId, AuditlogActions.VIEW, System.currentTimeMillis(), userId,
						String.format("User Id %d Viewed the Transaction of Account %s of User Id %d ", userId,
								accountNumber, targetUserId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(targetUserId, AuditlogActions.VIEW, System.currentTimeMillis(), userId,
						String.format("User Id %d Viewed the Transaction of Account %s of User Id %d but Failed", userId,
								accountNumber, targetUserId),
						Status.FAILURE);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Transaction Details",e);
			throw new CustomException("Exception Occured While Reterving Transaction Details", e);
		}
		return transactions;
	}

}
