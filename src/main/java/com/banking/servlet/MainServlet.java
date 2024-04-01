package com.banking.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banking.controller.UserController;
import com.banking.model.User;
import com.banking.model.UserType;
import com.banking.utils.CustomException;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public UserController userController;
	public static int employeeBranchId;

	public MainServlet() {
		super();
	}

	public void init() {
		this.userController = new UserController();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String action = request.getServletPath();
			RequestDispatcher dispatcher;
			switch (action) {
			case "/customer/account":
				dispatcher = request.getRequestDispatcher("/customer/account.jsp");
				dispatcher.forward(request, response);
				break;
			case "/customer/transaction":
				request.setAttribute("withinBank",
						((HttpServletRequest) request).getContextPath() + "/withinBankTransfer");
				dispatcher = request.getRequestDispatcher("/customer/transaction.jsp");
				dispatcher.forward(request, response);
				break;
			case "/customer/Statement":
				dispatcher = request.getRequestDispatcher("/customer/Statement.jsp");
				dispatcher.forward(request, response);
				break;
			case "/customer/profile":
				dispatcher = request.getRequestDispatcher("/customer/profile.jsp");
				dispatcher.forward(request, response);
				break;
			case "/employee/customer":
				dispatcher = request.getRequestDispatcher("/employee/customer.jsp");
				dispatcher.forward(request, response);
				break;
			case "/employee/account":
				dispatcher = request.getRequestDispatcher("/employee/account.jsp");
				dispatcher.forward(request, response);
				break;
			case "/employee/transaction":
				dispatcher = request.getRequestDispatcher("/employee/transaction.jsp");
				dispatcher.forward(request, response);
				break;
			case "/getcustomer":
				UserServletHelper.getCustomerDetails(request, response);
				dispatcher = request.getRequestDispatcher("/employee/customer.jsp");
				dispatcher.forward(request, response);
				break;
			case "/updateCustomer":
				UserServletHelper.getCustomerDetails(request, response);
				if (request.getAttribute("error") != null) {
					dispatcher = request.getRequestDispatcher("/employee/customer.jsp");
					dispatcher.forward(request, response);
				} else {
					request.getSession().setAttribute("employee", false);
					request.getSession().setAttribute("customer", false);
					request.setAttribute("updateAction", request.getContextPath() + "/updateUser");
					dispatcher = request.getRequestDispatcher("/employee/customerForm.jsp");
					dispatcher.forward(request, response);
				}
				break;
			case "/getAccounts":
				AccountServletHelper.getCustomerAccountsInBranch(request, response);
				dispatcher = request.getRequestDispatcher("/employee/account.jsp");
				dispatcher.forward(request, response);
				break;
			default:
				response.sendRedirect("404.jsp");
				break;
			}
		} catch (Exception e) {

		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getServletPath();
		RequestDispatcher dispatcher;
		switch (action) {
		case "/login":
			UserServletHelper.validateUser(request, response);
			String error = (String) request.getAttribute("error");
			if (error != null) {
				dispatcher = request.getRequestDispatcher("login.jsp");
				dispatcher.forward(request, response);
			} else {
				User user = (User) request.getSession(true).getAttribute("user");
				UserType userType = user.getTypeOfUser();
				switch (userType) {
				case CUSTOMER:
					AccountServletHelper.getCustomerAccounts(user.getUserId(), request, response);
					/*
					 * dispatcher = request.getRequestDispatcher("customer/account.jsp");
					 * dispatcher.forward(request, response); break;
					 */
					response.sendRedirect(request.getContextPath() + "/customer/account");
					break;
				case EMPLOYEE:
					try {
						// int employeeBranchId = userController.getEmployeeBranch(user.getUserId());
						request.getSession().setAttribute("employeeBranchId",
								userController.getEmployeeBranch(user.getUserId()));
						/*
						 * dispatcher = request.getRequestDispatcher("employee/customer.jsp");
						 * dispatcher.forward(request, response);
						 */
						response.sendRedirect(request.getContextPath() + "/employee/customer");
					} catch (CustomException e) {
						e.printStackTrace();
					}
					break;
				case ADMIN:
					/*
					 * dispatcher = request.getRequestDispatcher("employee/customer.jsp");
					 * dispatcher.forward(request, response);
					 */
					response.sendRedirect(request.getContextPath() + "/employee/customer");
					break;
				}
			}
			break;
		case "/logout":
			request.getSession().invalidate();
			response.sendRedirect("login.jsp");
			break;
		case "/addCustomer":
			request.getSession().setAttribute("customer", true);
			request.getSession().setAttribute("employee", false);
			request.setAttribute("createAction", request.getContextPath() + "/addUser");
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;

		case "/addEmployee":
			request.getSession().setAttribute("employee", true);
			request.getSession().setAttribute("customer", false);
			request.setAttribute("createAction", request.getContextPath() + "/addUser");
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;

		case "/addUser":
			UserServletHelper.addNewUser(request, response);
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;
		case "/updateUser":
			UserServletHelper.updateCustomer(request, response);
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;
		case "/createAccountPage":
			dispatcher = request.getRequestDispatcher("employee/accountForm.jsp");
			dispatcher.forward(request, response);
			break;
		case "/createAccount":
			AccountServletHelper.addNewAccount(request, response);
			dispatcher = request.getRequestDispatcher("employee/accountForm.jsp");
			dispatcher.forward(request, response);
			break;
		case "/getTransactions":
			TransactionServletHelper.getTransactions(request, response);
			dispatcher = request.getRequestDispatcher("employee/transaction.jsp");
			dispatcher.forward(request, response);
			break;
		case "/employeeDeposit":
			TransactionServletHelper.deposit(request, response);
			dispatcher = request.getRequestDispatcher("employee/transaction.jsp");
			dispatcher.forward(request, response);
			break;
		case "/employeeWithdraw":
			TransactionServletHelper.withdraw(request, response);
			dispatcher = request.getRequestDispatcher("employee/transaction.jsp");
			dispatcher.forward(request, response);
			break;
		case "/updateAccountStatus":
			TransactionServletHelper.updateAccountStatus(request, response);
			dispatcher = request.getRequestDispatcher("employee/account.jsp");
			dispatcher.forward(request, response);
			break;
		case "/switchAccount":
			dispatcher = request.getRequestDispatcher("customer/SwitchAccount.jsp");
			dispatcher.forward(request, response);
			break;
		case "/changeAccount":
			AccountServletHelper.changeAccount(request, response);
//			dispatcher = request.getRequestDispatcher("customer/account.jsp");
//			dispatcher.forward(request, response);

			response.sendRedirect(request.getContextPath() + "/customer/account");
			break;
		case "/transferOutSideBank":
			request.setAttribute("outSideBank", true);
			request.setAttribute("otherBank", request.getContextPath() + "/otherBankTransfer");
			dispatcher = request.getRequestDispatcher("customer/transaction.jsp");
			dispatcher.forward(request, response);
			break;
		case "/transferInBank":
			request.setAttribute("withinBank", request.getContextPath() + "/withinBankTransfer");
			dispatcher = request.getRequestDispatcher("customer/transaction.jsp");
			dispatcher.forward(request, response);
			break;
		case "/withinBankTransfer":
			TransactionServletHelper.transferAmountWithinBank(request, response);
			dispatcher = request.getRequestDispatcher("/transferInBank");
			dispatcher.forward(request, response);
			break;
		case "/otherBankTransfer":
			TransactionServletHelper.transferAmountWithotherBank(request, response);
			dispatcher = request.getRequestDispatcher("/transferOutSideBank");
			dispatcher.forward(request, response);
			break;
		case "/getStatements":
			TransactionServletHelper.getTransactions(request, response);
			dispatcher = request.getRequestDispatcher("customer/Statement.jsp");
			dispatcher.forward(request, response);
			break;
		case "/updatePassword":
			UserServletHelper.updatePassword(request, response);
			dispatcher = request.getRequestDispatcher("customer/profile.jsp");
			dispatcher.forward(request, response);
			break;
		default:
			response.sendRedirect("404.jsp");
			break;
		}
	}

}
