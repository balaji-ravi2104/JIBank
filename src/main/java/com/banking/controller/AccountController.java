package com.banking.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.cache.Cache;
import com.banking.cache.RedisCache;
import com.banking.dao.AccountDao;
import com.banking.dao.implementation.AccountDaoImplementation;
import com.banking.model.Account;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class AccountController {

	private static final Logger log = Logger.getLogger(MainController.class.getName());
	private AccountDao accountDao = new AccountDaoImplementation();
	private BranchController branchController = new BranchController();
	public static final String accountCachePrefix = "Account";
	public static final String listAccountCachePrefix = "ListAccount";

	private final Object createAccountLock = new Object();
	private final Object getAccountDetailsLock = new Object();
	private final Object getAccountsOfCustomerLock = new Object();

	// public static final Cache<String, Account> accountCache = new
	// LRUCache<String, Account>(50);
	// public static final Cache<Integer, List<Account>> listOfAccounts = new
	// LRUCache<Integer, List<Account>>(50);

	public static final Cache<String, Account> accountCache = new RedisCache<String, Account>(6379, accountCachePrefix);
	public static final Cache<Integer, List<Account>> listOfAccounts = new RedisCache<Integer, List<Account>>(6379,
			listAccountCachePrefix);

	public AccountController() {
	}

	public boolean createAccount(Account account,int creatingUserId) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAccountCreated = false;
		boolean isPrimary = false;
		synchronized (createAccountLock) {
			listOfAccounts.rem(listAccountCachePrefix + account.getUserId());
			if (!accountDao.customerHasAccount(account.getUserId())) {
				isPrimary = true;
			}
			try {
				isAccountCreated = accountDao.createAccount(account, isPrimary,creatingUserId);
			} catch (Exception e) {
				throw new CustomException("Erroe While Creating Account!!", e);
			}
		}
		return isAccountCreated;
	}

	public boolean isAccountExistsInTheBranch(String accountNumber, int branchId) throws CustomException {
		InputValidator.isNull(accountNumber, "Account Number Cannot be Null!!!");
		boolean isAccountExists = false;
		if (validateAccountNumber(accountNumber) || !branchController.validateBranchId(branchId)) {
			return isAccountExists;
		}
		try {
			isAccountExists = accountDao.checkAccountExists(accountNumber, branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking Account", e);
		}
		return isAccountExists;
	}

	public Account getAccountDetails(String accountNumber) throws CustomException {
		InputValidator.isNull(accountNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		Account account = null;
		synchronized (getAccountDetailsLock) {
			if (accountCache.get(accountCachePrefix + accountNumber) != null) {
				return accountCache.get(accountCachePrefix + accountNumber);
			}
			try {
				account = accountDao.getAccountDetail(accountNumber);
				if (account != null) {
					accountCache.set(accountNumber, account);
				}
			} catch (Exception e) {
				throw new CustomException("Error while Reterving Account Details !!", e);
			}
		}
		return account;
	}

	public List<Account> getAccountsOfCustomer(int userId) throws CustomException {
		List<Account> accounts = null;
		synchronized (getAccountsOfCustomerLock) {
			if (listOfAccounts.get(listAccountCachePrefix + userId) != null) {
				return listOfAccounts.get(listAccountCachePrefix + (userId));
			}
			try {
				accounts = accountDao.getAllAccountsOfCustomer(userId);
				listOfAccounts.set(userId, accounts);
			} catch (Exception e) {
				throw new CustomException("Error while Reterving Accounts!!", e);
			}
		}
		return accounts;
	}

	public Map<String, Account> getCustomerAccountsInBranch(int userId, int branchId) throws CustomException {
		Map<String, Account> customerAccounts = null;
		try {
			customerAccounts = accountDao.getCustomerAccounts(userId, branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Reterving Customer Accounts!!", e);
		}
		return customerAccounts;
	}

	public Map<Integer, Map<String, Account>> getCustomerAccountsInAllBranch(int userId) throws CustomException {
		Map<Integer, Map<String, Account>> customerAccounts = null;
		try {
			customerAccounts = accountDao.getCustomersAllAccount(userId);
		} catch (Exception e) {
			throw new CustomException("Error while Reterving Customer Accounts!!", e);
		}
		return customerAccounts;
	}

	public boolean activateDeactivateCustomerAccount(String accountNumber, int status,int updatingUserId)
			throws CustomException {
		InputValidator.isNull(accountNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAccountStatusChanged = false;
		try {
			isAccountStatusChanged = accountDao.activateDeactivateCustomerAccount(accountNumber, status,updatingUserId);
		} catch (Exception e) {
			throw new CustomException("Error while Updating Bank Account Status!!", e);
		}
		return isAccountStatusChanged;
	}

	public boolean isUserAlreadyHasAccount(int userId, int accountType, int branchId) throws CustomException {
		boolean isAccountPresent = false;
		try {
			isAccountPresent = accountDao.isCustomerAlreadyHasAccount(userId, accountType, branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking Bank Account Type Exists!!", e);
		}
		return isAccountPresent;
	}

	public boolean isAccountPresent(String accountNumber) throws CustomException{
		boolean isAccountPresent = false;
		try {
			isAccountPresent = accountDao.isAccountPresent(accountNumber);
		} catch (Exception e) {
			throw new CustomException("Error while Checking Bank Account Type Exists!!", e);
		}
		return isAccountPresent;
	}

	private boolean validateAccountNumber(String accountNumber) throws CustomException {
		boolean isValid = false;
		if (InputValidator.validateString(accountNumber)) {
			log.warning("Account Number Cannot be Empty!!!");
			isValid = true;
		}
		return isValid;
	}

	public boolean validateAccountAndBranch(String accountNumber, int branchId) throws CustomException {
		boolean isValid = true;
		if (!isAccountExistsInTheBranch(accountNumber, branchId)) {
			log.log(Level.WARNING, "Account Number Doesn't Exists in this Branch!!!");
			isValid = false;
		}
		return isValid;
	}
}
