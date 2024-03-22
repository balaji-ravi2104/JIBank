package com.banking.dao;

import java.util.List;
import java.util.Map;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.utils.CustomException;

public interface TransactionDao {

	boolean deposit(Account selectedAccount, double amountToDeposite) throws CustomException;

	boolean withdraw(Account selectedAccount, double amountToWithdraw) throws CustomException;

	boolean transferMoneyWithinBank(Account accountFromTransfer, Account accountToTransfer, double amountToTransfer,
			String remark) throws CustomException;

	boolean transferMoneyWithOtherBank(Account accountFromTransfer, String accountNumberToTransfer,
			double amountToTransferWithOtherBank, String remark) throws CustomException;

	List<Transaction> getUsersStatement(Account account, int numberOfMonths) throws CustomException;

	List<Transaction> getCustomerTransactionHistory(String accountNumber, int month) throws CustomException;

	Map<String, List<Transaction>> getAllTransactionHistory(int userId, int branchId, int month) throws CustomException;

}
