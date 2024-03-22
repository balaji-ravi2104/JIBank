package com.banking.view;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.banking.model.Transaction;
import com.banking.utils.CustomException;
import com.banking.utils.DateUtils;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class TransactionView {

	private static final Logger log = Logger.getLogger(TransactionView.class.getName());

	public void transactionMessages(String message) {
		log.info(message);
	}

	public void displayStatements(List<Transaction> statement) throws CustomException {
		InputValidator.isNull(statement, ErrorMessages.INPUT_NULL_MESSAGE);
		if (statement.isEmpty()) {
			transactionMessages("No Statement Avaliable For your Account!!!");
			return;
		}
		for (Transaction transaction : statement) {
			log.info("-".repeat(60));
			log.info("Date : " + DateUtils.formateLongToDate(transaction.getDateOfTransaction()));
			log.info("Transaction Type : " + transaction.getTransactionType());
			log.info("Transaction Amount : " + transaction.getTransactedAmount());
			log.info("Balance : " + transaction.getBalance());
			log.info("-".repeat(60));
		}
	}

	public void displayTransactionHistory(List<Transaction> transactionsHistory) throws CustomException {
		InputValidator.isNull(transactionsHistory, ErrorMessages.INPUT_NULL_MESSAGE);
		if (transactionsHistory.isEmpty()) {
			transactionMessages("No transaction history available!!");
			return;
		}
		log.info("-".repeat(150));
		log.info(String.format(
				"| %-12s | %-8s | %-18s | %-18s | %-18s | %-15s | %-15s | %-15s | %-20s | %-20s | %-10s |",
				"TransactionId", "UserId", "ReferenceId", "ViewerAccount", "TransactedAccount", "TransactionType",
				"TransactionAmount", "Balance", "TransactionDate", "Remark", "Status"));
		log.info("-".repeat(150));
		for (Transaction transaction : transactionsHistory) {
			log.info(String.format(
					"| %-12d | %-8d | %-18d | %-18s | %-18s | %-15s | %-15.2f | %-15.2f | %-20s | %-20s | %-10s |",
					transaction.getTransactionId(), transaction.getUserId(), transaction.getReferenceId(),
					transaction.getViewerAccount(), transaction.getTransactedAccount(),
					transaction.getTransactionType(), transaction.getTransactedAmount(), transaction.getBalance(),
					DateUtils.formateLongToDate(transaction.getDateOfTransaction()), transaction.getRemark(),
					transaction.getStatus(), transaction.getReferenceId()));
		}
		log.info("-".repeat(150));
	}

	public void displayAllTransActionHistory(Map<String, List<Transaction>> allTransactionHistoryMap)
			throws CustomException {
		if (allTransactionHistoryMap.isEmpty()) {
			transactionMessages("No transaction history available!!");
			return;
		}
		for (String accountNumber : allTransactionHistoryMap.keySet()) {
			List<Transaction> transactionList = allTransactionHistoryMap.get(accountNumber);
			log.info("Transaction History OF Account Number :" + accountNumber);
			displayTransactionHistory(transactionList);
		}
	}

	public void displayTransactionsHistory(Map<Integer, Map<String, List<Transaction>>> allTransactionsHistory)
			throws CustomException {
		if (allTransactionsHistory.isEmpty()) {
			transactionMessages("No transaction history available!!");
			return;
		}
		for (Map.Entry<Integer, Map<String, List<Transaction>>> entry : allTransactionsHistory.entrySet()) {
			int userId = entry.getKey();
			log.info("Transaction History Of User Id : " + userId);
			displayAllTransActionHistory(entry.getValue());
		}
	}

	public void displayTransactionByBranch(Map<Integer, Map<String, List<Transaction>>> transactionsOfCustomer)
			throws CustomException {
		if (transactionsOfCustomer.isEmpty()) {
			transactionMessages("No transaction history available!!");
			return;
		}
		for (Map.Entry<Integer, Map<String, List<Transaction>>> entry : transactionsOfCustomer.entrySet()) {
			int branchId = entry.getKey();
			log.info("Transaction History Of Branch Id : " + branchId);
			displayAllTransActionHistory(entry.getValue());
		}
	}

}
