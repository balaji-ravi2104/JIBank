package com.banking.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banking.controller.AccountController;
import com.banking.model.Account;
import com.banking.utils.CustomException;

public class AccountServletHelper {
	private static AccountController accountController = new AccountController();

	public static void getCustomerAccountsr(HttpServletRequest request, HttpServletResponse response) {
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
			e.printStackTrace();
		}
	}

	public static void addNewAccount(HttpServletRequest request, HttpServletResponse response) {
		Account account = (Account) request.getAttribute("accountObject");
		try {
			boolean isAccountCreated = accountController.createAccount(account);
			if(isAccountCreated) {
				request.setAttribute("success", "Account Created Successfully");
			}else {
				request.setAttribute("failure", "Account Creation Failed");
			}
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
}
