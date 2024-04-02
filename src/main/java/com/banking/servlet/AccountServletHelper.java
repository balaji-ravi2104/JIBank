package com.banking.servlet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.controller.AccountController;
import com.banking.model.Account;
import com.banking.utils.CustomException;

public class AccountServletHelper {
	private static AccountController accountController = new AccountController();

	public static void getCustomerAccountsInBranch(HttpServletRequest request, HttpServletResponse response) {
		int userId = Integer.parseInt(request.getParameter("userId"));
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		try {
			Map<String, Account> customerAccounts = accountController.getCustomerAccountsInBranch(userId, branchId);
			if (customerAccounts.isEmpty()) {
				request.setAttribute("error", "No Accounts Found");
			} else {
				request.setAttribute("customerAccounts", customerAccounts);
			}
		} catch (CustomException e) {
			request.setAttribute("error", "An Error Occured, Try Again");
		}
	}

	public static void addNewAccount(HttpServletRequest request, HttpServletResponse response) {
		Account account = (Account) request.getAttribute("accountObject");
		try {
			boolean isAccountCreated = accountController.createAccount(account);
			if (isAccountCreated) {
				request.setAttribute("success", "Account Created Successfully");
			} else {
				request.setAttribute("failure", "Account Creation Failed");
			}
		} catch (CustomException e) {
			request.setAttribute("failure", "Account Creation Failed");
		}
	}

	public static void getCustomerAccounts(int userId, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Account> accounts = accountController.getAccountsOfCustomer(userId);
			if (accounts.isEmpty()) {
				request.setAttribute("noaccounts", "No Account Found");
			} else {
				for (Account account : accounts) {
					if (account.isPrimaryAccount()) {
						request.getSession(true).setAttribute("currentAccount", account);
						break;
					}
				}
				request.getSession(true).setAttribute("accountsList", accounts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void changeAccount(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String accountNumber = (String) request.getParameter("accountNumber");
			List<Account> accounts = (List<Account>) session.getAttribute("accountsList");
			for (Account account : accounts) {
				if (account.getAccountNumber().equals(accountNumber)) {
					session.setAttribute("currentAccount", account);
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Account isReceiverIsSame(String accountNumber, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			List<Account> accounts = (List<Account>) session.getAttribute("accountsList");
			for (Account account : accounts) {
				if (account.getAccountNumber().equals(accountNumber)) {
					return account;
				}
			}
		}
		return null;
	}
}
