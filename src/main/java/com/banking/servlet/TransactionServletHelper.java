package com.banking.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banking.controller.AccountController;
import com.banking.controller.TransactionController;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Transaction;

public class TransactionServletHelper {

	private static final TransactionController transactionController = new TransactionController();
	private static final AccountController accountController = new AccountController();

	public static void getTransactions(HttpServletRequest request, HttpServletResponse response) {
		String accountNumber = request.getParameter("accountNumber");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");

		try {
			List<Transaction> transactions = transactionController.getTransactions(accountNumber, fromDate, toDate);
			if (transactions.isEmpty() || transactions == null) {
				request.setAttribute("message", "No Transaction Found");
			} else {
				request.setAttribute("transactionList", transactions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deposit(HttpServletRequest request, HttpServletResponse response) {
		Account account = (Account) request.getAttribute("account");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String description = request.getParameter("description");
		try {
			boolean isAmountDeposited = transactionController.depositAmount(account, amount, description);
			if (isAmountDeposited) {
				request.setAttribute("success", "Amount Deposited Successfully");
			} else {
				request.setAttribute("failed", "Amount Deposit Failed");
			}
		} catch (Exception e) {

		}
	}

	public static void withdraw(HttpServletRequest request, HttpServletResponse response) {
		Account account = (Account) request.getAttribute("account");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String description = request.getParameter("description");
		try {
			boolean isAmountDeposited = transactionController.withdrawAmount(account, amount, description);
			if (isAmountDeposited) {
				request.setAttribute("success", "Amount Withdraw Successfully");
			} else {
				request.setAttribute("failed", "Amount Withdraw Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateAccountStatus(HttpServletRequest request, HttpServletResponse response) {
		String accountNumber = request.getParameter("accountNumber");
		String status = request.getParameter("status");

		String oppositeStatus = status.equalsIgnoreCase("ACTIVE") ? AccountStatus.INACTIVE.name()
				: AccountStatus.ACTIVE.name();

		AccountStatus accountStatus = AccountStatus.fromString(oppositeStatus);
		int value = accountStatus.getValue();

		try {
			boolean isUpdated = accountController.activateDeactivateCustomerAccount(accountNumber, value);
			if (isUpdated) {
				request.setAttribute("userId", request.getAttribute("userId"));
				request.setAttribute("branchId", request.getAttribute("branchId"));
				request.setAttribute("updatedSuccess", "Account Status Updated");
			} else {
				request.setAttribute("updationFailed", "Account Updation Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
