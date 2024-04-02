package com.banking.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banking.controller.AccountController;
import com.banking.controller.TransactionController;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Transaction;
import com.banking.utils.CustomException;

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
		} catch (CustomException e) {
			//e.printStackTrace();
			request.setAttribute("message", "An Error Occured, Try Again");
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
			request.setAttribute("failed", "Amount Deposit Failed");
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
			//e.printStackTrace();
			request.setAttribute("failed", "Amount Withdraw Failed");
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
				request.setAttribute("updatedSuccess", "Account Status Updated");
			} else {
				request.setAttribute("updationFailed", "Account Updation Failed");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			request.setAttribute("updationFailed", "Account Updation Failed");
		}
	}

	public static void transferAmountWithinBank(HttpServletRequest request, HttpServletResponse response) {
		Account senderAccount = (Account) request.getAttribute("senderAccount");
		Account receiverAccount = (Account) request.getAttribute("receiverAccount");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String remark = request.getParameter("message");
		try {
			boolean isAmountTransfered = transactionController.transferWithinBank(senderAccount, receiverAccount,
					amount, remark);
			if (isAmountTransfered) {
				request.setAttribute("success", "Amount Transfered Successfully");
			} else {
				request.setAttribute("failed", "Amount Transaction Failed");
			}
		} catch (Exception e) {
			request.setAttribute("failed", "Amount Transaction Failed");
			//e.printStackTrace();
		}

	}

	public static void transferAmountWithotherBank(HttpServletRequest request, HttpServletResponse response) {
		String receiverAccountNumber = request.getParameter("accountNumber");
		Account senderAccount = (Account) request.getAttribute("senderAccount");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String remark = request.getParameter("message");
		try {
			boolean isAmountTransfered = transactionController.transferWithOtherBank(senderAccount,
					receiverAccountNumber, amount, remark);
			if (isAmountTransfered) {
				request.setAttribute("success", "Amount Transfered Successfully");
			} else {
				request.setAttribute("failed", "Amount Transaction Failed");
			}
		} catch (Exception e) {
			request.setAttribute("failed", "Amount Transaction Failed");
			//e.printStackTrace();
		}
	}

}