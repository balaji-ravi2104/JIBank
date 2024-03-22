package com.banking.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banking.controller.UserController;
import com.banking.model.Customer;
import com.banking.model.User;
import com.banking.utils.CustomException;

public class UserServletHelper {

	public static UserController userController = new UserController();

	public static void validateUser(HttpServletRequest request, HttpServletResponse response) {
		int userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");

		try {
			User user = userController.login(userId, password);
			// System.out.println(user);
			if (user == null) {
				request.setAttribute("error", "Invalid User Id or Password!!");
			} else {
				request.getSession(true).setAttribute("user", user);
			}
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}

	public static void getCustomerDetails(HttpServletRequest request, HttpServletResponse response) {
		int customerId = Integer.parseInt(request.getParameter("userId"));
		System.out.println(customerId);
		try {
			Customer customer = userController.getCustomerDetailsById(customerId);
			if (customer == null) {
				request.setAttribute("error", "Invalid Customer Id");
			} else {
				request.setAttribute("customerDetails", customer);
			}
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}

}
