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

//@WebServlet("/")
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
		String action = request.getServletPath();
		RequestDispatcher dispatcher;
		try {
			switch (action) {
			case "/getcustomer":
				UserServletHelper.getCustomerDetails(request, response);
				dispatcher = request.getRequestDispatcher("/employee/customer.jsp");
				dispatcher.forward(request, response);
				break;
			case "/updateCustomer":
				request.getSession().setAttribute("employee", false);
				request.getSession().setAttribute("customer", false);
				dispatcher = request.getRequestDispatcher("/employee/customerForm.jsp");
				dispatcher.forward(request, response);
				break;
			default:
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
				dispatcher = request.getRequestDispatcher("/login.jsp");
				dispatcher.forward(request, response);
			} else {
				User user = (User) request.getSession().getAttribute("user");
				UserType userType = user.getTypeOfUser();
				switch (userType) {
				case CUSTOMER:
					dispatcher = request.getRequestDispatcher("customer/home.jsp");
					dispatcher.forward(request, response);
					break;
				case EMPLOYEE:
				case ADMIN:
					dispatcher = request.getRequestDispatcher("employee/customer.jsp");
					dispatcher.forward(request, response);
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
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;

		case "/addEmployee":
			request.getSession().setAttribute("employee", true);
			request.getSession().setAttribute("customer", false);
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;

		case "/addUser":
			UserServletHelper.addNewUser(request, response);
			dispatcher = request.getRequestDispatcher("employee/customerForm.jsp");
			dispatcher.forward(request, response);
			break;
		}
	}

}
