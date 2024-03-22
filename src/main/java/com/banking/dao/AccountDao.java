package com.banking.dao;

import java.util.List;
import java.util.Map;

import com.banking.model.Account;
import com.banking.utils.CustomException;

public interface AccountDao {

	boolean createAccount(Account account, boolean isPrimary) throws CustomException;

	boolean checkAccountExists(String accountNumber, int branchId) throws CustomException;

	Account getAccountDetail(String accountNumber) throws CustomException;

	List<Account> getAllAccountsOfCustomer(int userId) throws CustomException;

	boolean activateDeactivateCustomerAccount(String accountNumber, int branchId, int status) throws CustomException;

	Map<String, Account> getCustomerAccounts(int userId, int branchId) throws CustomException;

	Map<Integer, Map<String, Account>> getCustomersAllAccount(int userId) throws CustomException;

	boolean customerHasAccount(int userId) throws CustomException;
}
