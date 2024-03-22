package com.banking.controller;

import java.util.List;
import java.util.Map;

import com.banking.dao.TransactionDao;
import com.banking.dao.implementation.TransactionDaoImplementation;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Transaction;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.view.TransactionView;

public class TransactionController {

	private TransactionDao transactionDao;
	private AccountController accountController;
	private BranchController branchController;
	private UserController userController;
	private TransactionView transactionView;

	private static final Object accountCacheLock = new Object();
	private static final Object listOfAccountsLock = new Object();

	public TransactionController() {
		this.accountController = new AccountController();
		this.transactionDao = new TransactionDaoImplementation();
		this.userController = new UserController();
		this.branchController = new BranchController();
		this.transactionView = new TransactionView();
	}

	public boolean depositAmount(Account account, double amountToDeposite) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isDepositeSuccess = false;
		if (!validateAmount(amountToDeposite)) {
			return isDepositeSuccess;
		}

		synchronized (accountCacheLock) {
			AccountController.accountCache.rem(AccountController.accountCachePrefix + account.getAccountNumber());
		}
		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts.rem(AccountController.listAccountCachePrefix + account.getUserId());
		}

		try {
			isDepositeSuccess = transactionDao.deposit(account, amountToDeposite);
		} catch (Exception e) {
			throw new CustomException("Error while Depositing Money!!", e);
		}
		return isDepositeSuccess;
	}

	public boolean withdrawAmount(Account account, double amountToWithdraw) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isWithdrawSuccess = false;
		if (!validateAmount(amountToWithdraw) || !validateWithdrawAmount(account, amountToWithdraw)) {
			return isWithdrawSuccess;
		}

		synchronized (accountCacheLock) {
			AccountController.accountCache.rem(AccountController.accountCachePrefix + account.getAccountNumber());
		}
		synchronized (listOfAccountsLock) {
			AccountController.listOfAccounts.rem(AccountController.listAccountCachePrefix + account.getUserId());
		}

		try {
			isWithdrawSuccess = transactionDao.withdraw(account, amountToWithdraw);
		} catch (Exception e) {
			throw new CustomException("Error while Depositing Money!!", e);
		}
		return isWithdrawSuccess;
	}

	public boolean transferWithinBank(Account accountFromTransfer, Account accountToTransfer, double amountToTransfer,
			String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(remark, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isTransactionSuccess = false;
		if (!validateAmount(amountToTransfer) || !validateWithdrawAmount(accountFromTransfer, amountToTransfer)) {
			return isTransactionSuccess;
		}
		if (accountToTransfer.getAccountStatus() == AccountStatus.INACTIVE) {
			transactionView.transactionMessages("The Account is INACTIVE!! Please Try With Different Account!!");
			return isTransactionSuccess;
		}

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
			throw new CustomException("Error while Transferring Money!! " + e.getMessage(), e);
		}
		return isTransactionSuccess;
	}

	public boolean transferWithOtherBank(Account accountFromTransfer, String accountNumberToTransfer,
			double amountToTransferWithOtherBank, String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountNumberToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(remark, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isTransactionSuccess = false;
		if (!validateAmount(amountToTransferWithOtherBank)
				|| !validateWithdrawAmount(accountFromTransfer, amountToTransferWithOtherBank)) {
			return isTransactionSuccess;
		}

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
			throw new CustomException("Error while Transferring Money!! " + e.getMessage(), e);
		}
		return isTransactionSuccess;
	}

	public List<Transaction> getStatement(Account account, int numberOfMonths) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		List<Transaction> statement = null;
		if (!validateMonths(numberOfMonths)) {
			return statement;
		}
		try {
			statement = transactionDao.getUsersStatement(account, numberOfMonths);
		} catch (Exception e) {
			throw new CustomException("Error while Getting Transaction!!!", e);
		}
		return statement;
	}

	public List<Transaction> getCustomerTransaction(String accountNumber, int branchId, int months)
			throws CustomException {
		InputValidator.isNull(accountNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		List<Transaction> transactionHistory = null;
		if (!validateMonths(months) || !branchController.validateBranchId(branchId)
				|| !accountController.validateAccountAndBranch(accountNumber, branchId)) {
			return transactionHistory;
		}
		try {
			transactionHistory = transactionDao.getCustomerTransactionHistory(accountNumber, months);
		} catch (Exception e) {
			throw new CustomException("Error while Getting Transaction History!!!", e);
		}
		return transactionHistory;
	}

	public Map<String, List<Transaction>> getAllTransactionsOfCustomer(int userId, int branchId, int month)
			throws CustomException {
		Map<String, List<Transaction>> transactions = null;
		if (!validateMonths(month) || !userController.validateUserIdAndBranchId(userId, branchId)) {
			return transactions;
		}
		try {
			transactions = transactionDao.getAllTransactionHistory(userId, branchId, month);
		} catch (Exception e) {
			throw new CustomException("Error while Getting Transaction History!!!", e);
		}
		return transactions;
	}

	private boolean validateAmount(double amountToDeposite) {
		boolean isValid = true;
		if (amountToDeposite <= 0) {
			transactionView
					.transactionMessages("Deposite or Withdrawal or Transfer Amount Should be greater than ZERO!!");
			isValid = false;
		}
		return isValid;
	}

	private boolean validateWithdrawAmount(Account account, double amountToWithdraw) {
		boolean isValid = true;
		if (amountToWithdraw > account.getBalance()) {
			transactionView.transactionMessages("Insufficient Balance!! Can't able to Tranfer or Withdraw!!!");
			isValid = false;
		}
		return isValid;
	}

	private boolean validateMonths(int numberOfMonths) {
		boolean isValid = true;
		if (numberOfMonths <= 0 || numberOfMonths > 6) {
			transactionView.transactionMessages("Please Enter the Valid Month.. From 1 to 6..");
			isValid = false;
		}
		return isValid;
	}

}
