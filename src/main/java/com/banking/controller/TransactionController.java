package com.banking.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.AccountDao;
import com.banking.dao.TransactionDao;
import com.banking.dao.implementation.AccountDaoImplementation;
import com.banking.dao.implementation.TransactionDaoImplementation;
import com.banking.model.Account;
import com.banking.model.Status;
import com.banking.model.Transaction;
import com.banking.utils.AuditLogUtils;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class TransactionController {

	private TransactionDao transactionDao;
	private AccountDao accountDao;

	private static final Logger logger = LoggerProvider.getLogger();

	private static final Object accountCacheLock = new Object();
	private static final Object listOfAccountsLock = new Object();

	public TransactionController() {
		this.accountDao = new AccountDaoImplementation();
		this.transactionDao = new TransactionDaoImplementation();
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Depositing Money", e);
			throw new CustomException("Exception Occured While Depositing Money", e);
		} finally {
			AuditLogUtils.logAmountDeposit(userId, account, isDepositeSuccess ? Status.SUCCESS : Status.FAILURE);
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Withdrawing Money", e);
			throw new CustomException("Exception Occured While Withdrawing Money", e);
		} finally {
			AuditLogUtils.logAmountWithdraw(userId, account, isWithdrawSuccess ? Status.SUCCESS : Status.FAILURE);
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Transferring Money Within Bank", e);
			throw new CustomException("Exception Occured While Transferring Money Within Bank", e);
		} finally {
			AuditLogUtils.logAmountTransfer(accountFromTransfer, accountToTransfer.getAccountNumber(),
					isTransactionSuccess ? Status.SUCCESS : Status.FAILURE);
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Transferring Money With Other Bank", e);
			throw new CustomException("Exception Occured While Transferring Money With Other Bank", e);
		} finally {
			AuditLogUtils.logAmountTransfer(accountFromTransfer, accountNumberToTransfer,
					isTransactionSuccess ? Status.SUCCESS : Status.FAILURE);
		}
		return isTransactionSuccess;
	}

	public List<Transaction> getTransactions(String accountNumber, String startDate, String endDate, int userId)
			throws CustomException {
		List<Transaction> transactions = null;
		int targetUserId = 0;
		try {
			transactions = transactionDao.getCustomerTransactions(accountNumber, startDate, endDate, userId);
			targetUserId = accountDao.getAccountDetail(accountNumber).getUserId();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Transaction Details", e);
			throw new CustomException("Exception Occured While Reterving Transaction Details", e);
		} finally {
			AuditLogUtils.logTransactionView(userId, targetUserId, accountNumber,
					transactions != null ? Status.SUCCESS : Status.FAILURE);
		}
		return transactions;
	}

}
