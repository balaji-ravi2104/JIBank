package com.banking.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.controller.AccountController;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.utils.CustomException;

public class AccountServletHelper {
	private static AccountController accountController = new AccountController();

	public static void getCustomerAccountsInBranch(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
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
		HttpSession session = request.getSession(false);
		try {
			if (session != null) {
				Account account = (Account) request.getAttribute("accountObject");
				int creatingUserId = (int) request.getSession().getAttribute("currentUserId");
				boolean isAccountCreated = accountController.createAccount(account, creatingUserId);
				if (isAccountCreated) {
					request.setAttribute("success", "Account Created Successfully");
				} else {
					request.setAttribute("failure", "Account Creation Failed");
				}
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
				request.getSession(true).setAttribute("accountsCount", accounts.size());
			}
		} catch (Exception e) {
			request.setAttribute("noaccounts", "No Account Found");
		}
	}

	public static void updateAccountStatus(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);

		try {
			if (session != null) {
				String accountNumber = request.getParameter("accountNumber");
				String status = request.getParameter("status");

				String oppositeStatus = status.equalsIgnoreCase("ACTIVE") ? AccountStatus.INACTIVE.name()
						: AccountStatus.ACTIVE.name();

				AccountStatus accountStatus = AccountStatus.fromString(oppositeStatus);
				int value = accountStatus.getValue();
				int updatingUserId = (int) session.getAttribute("currentUserId");
				boolean isUpdated = accountController.changeAccountStatus(accountNumber, value, updatingUserId);
				if (isUpdated) {
					request.setAttribute("updatedSuccess", "Account Status Updated");
				} else {
					request.setAttribute("updationFailed", "Account Updation Failed");
				}
			}
		} catch (Exception e) {
			request.setAttribute("updationFailed", "Account Updation Failed");
		}
	}

	public static void changeAccount(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String accountNumber = (String) request.getParameter("accountNumber");
			int userId = (int) session.getAttribute("currentUserId");
			List<Account> accounts;
			try {
				accounts = accountController.getAccountsOfCustomer(userId);
				for (Account account : accounts) {
					if (account.getAccountNumber().equals(accountNumber)) {
						session.setAttribute("currentAccount", account);
						break;
					}
				}
			} catch (CustomException e) {
				e.printStackTrace();
			}
		}
	}
}
