package com.banking.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.controller.AccountController;
import com.banking.controller.TransactionController;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.utils.CustomException;

public class TransactionServletHelper {

	private static final TransactionController transactionController = new TransactionController();
	private static final AccountController accountController = new AccountController();

	public static void getTransactions(HttpServletRequest request, HttpServletResponse response) {
		String accountNumber = request.getParameter("accountNumber");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		int userId = UserServletHelper.getCurrentUserId(request, response);
		try {
			List<Transaction> transactions = transactionController.getTransactions(accountNumber, fromDate, toDate,userId);
			if (transactions.isEmpty() || transactions == null) {
				request.setAttribute("message", "No Transaction Found");
			} else {
				request.setAttribute("transactionList", transactions);
			}
		} catch (CustomException e) {
			request.setAttribute("message", "An Error Occured, Try Again");
		}
	}

	public static void deposit(HttpServletRequest request, HttpServletResponse response) {
		Account account = (Account) request.getAttribute("account");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String description = request.getParameter("description");
		int userId = UserServletHelper.getCurrentUserId(request, response);
		try {
			boolean isAmountDeposited = transactionController.depositAmount(account, amount, description,userId);
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
		int userId = UserServletHelper.getCurrentUserId(request, response);
		
		try {
			boolean isAmountDeposited = transactionController.withdrawAmount(account, amount, description,userId);
			if (isAmountDeposited) {
				request.setAttribute("success", "Amount Withdraw Successfully");
			} else {
				request.setAttribute("failed", "Amount Withdraw Failed");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			request.setAttribute("failed", "Amount Withdraw Failed");
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
				HttpSession session = request.getSession(false);
				session.setAttribute("currentAccount", accountController.getAccountDetails(senderAccount.getAccountNumber()));
			} else {
				request.setAttribute("failed", "Amount Transaction Failed");
			}
		} catch (Exception e) {
			request.setAttribute("failed", "Amount Transaction Failed");
			e.printStackTrace();
		}

	}

	public static void transferAmountWithotherBank(HttpServletRequest request, HttpServletResponse response) {
		String receiverAccountNumber = request.getParameter("accountNumber");
		Account senderAccount = (Account) request.getAttribute("senderAccount");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String remark = request.getParameter("message");
		HttpSession session = request.getSession(false);
		try {
			boolean isAmountTransfered = transactionController.transferWithOtherBank(senderAccount,
					receiverAccountNumber, amount, remark);
			if (isAmountTransfered) {
				request.setAttribute("success", "Amount Transfered Successfully");
				session.setAttribute("currentAccount", accountController.getAccountDetails(senderAccount.getAccountNumber()));
			} else {
				request.setAttribute("failed", "Amount Transaction Failed");
			}
		} catch (Exception e) {
			request.setAttribute("failed", "Amount Transaction Failed");
			// e.printStackTrace();
		}
	}

}
