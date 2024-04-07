package com.banking.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.controller.UserController;
import com.banking.model.AccountStatus;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.SessionDetails;
import com.banking.model.User;
import com.banking.model.UserType;
import com.banking.utils.CustomException;
import com.banking.utils.DateUtils;

public class UserServletHelper {

	private static UserController userController = new UserController();
	

	public static void loginUser(HttpServletRequest request, HttpServletResponse response) {
		int userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");

		try {
			User user = userController.login(userId, password);
			if (user == null) {
				request.setAttribute("error", "Invalid User Id or Password");
			} else if (user.getStatus() == AccountStatus.INACTIVE) {
				request.setAttribute("error", "Your Account is InActive");
			} else {
				HttpSession session = request.getSession(true);
				session.setAttribute("currentUserId", user.getUserId());
				session.setAttribute("user", user);
				boolean isSessionLogged = userController.logSessionData(getSessionObject(session,user.getUserId(),request));
				if (!isSessionLogged) {
					request.setAttribute("error", "A problem occured, Try after sometime");
				}
			}
		} catch (CustomException e) {
			System.out.println("In Exception");
			request.setAttribute("error", "A problem occured, Try after sometime");
		}
	}

	private static SessionDetails getSessionObject(HttpSession session, int userId, HttpServletRequest request) {
		SessionDetails sessionDetails = new SessionDetails();
		sessionDetails.setSessionId(session.getId());
		sessionDetails.setUserId(userId);
		sessionDetails.setUserAgent(request.getHeader("User-Agent"));
		sessionDetails.setLoginTime(System.currentTimeMillis());
		return sessionDetails;
	}

	public static boolean updateLogoutSession(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		boolean isUpdated = false;
		try {
			if (session != null) {
				int userId = (int) session.getAttribute("currentUserId");
				isUpdated = userController.updateLogoutSession(session.getId(), userId);
			}
		} catch (Exception e) {
			request.setAttribute("logoutError", "An problem occured while logout");
		}
		return isUpdated;
	}

	public static void getCustomerDetails(HttpServletRequest request, HttpServletResponse response) {
		int customerId = Integer.parseInt(request.getParameter("userId"));
		try {
			Customer customer = userController.getCustomerDetailsById(customerId);
			if (customer == null || customer.getTypeOfUser() == UserType.ADMIN) {
				request.setAttribute("error", "Invalid Customer Id");
			} else {
				String date = DateUtils.longToDate(customer.getDateOfBirth());
				date = DateUtils.convertToHtmlDateFormat(date);
				request.setAttribute("DOB", date);
				request.setAttribute("customerDetails", customer);
			}
		} catch (CustomException e) {
			request.setAttribute("error", "An Error Occured, Try Again");
		}
	}

	public static void addNewUser(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		try {
			if (session != null) {
				Boolean isCustomer = (Boolean) session.getAttribute("customer");
				Boolean isEmployee = (Boolean) session.getAttribute("employee");
				int creatingUserId = (int) session.getAttribute("currentUserId");

				if (isCustomer != null && isCustomer) {
					Customer customer = (Customer) request.getAttribute("customerObject");
					boolean isCreated = userController.registerNewCustomer(customer, creatingUserId);
					if (isCreated) {
						request.setAttribute("userCreationSuccess", "User Created Successfully!!!");
					} else {
						request.setAttribute("userCreationFailed", "User Creation Failed!! Try Again!!");
					}

				} else if (isEmployee != null && isEmployee) {
					Employee employee = (Employee) request.getAttribute("employeeObject");
					boolean isCreated = userController.registerNewEmployee(employee, creatingUserId);
					if (isCreated) {
						request.setAttribute("userCreationSuccess", "Employee Created Successfully!!!");
					} else {
						request.setAttribute("userCreationFailed", "Employee Creation Failed!! Try Again!!");
					}
				}
			}
		} catch (CustomException e) {
			request.setAttribute("userCreationFailed", "User Creation Failed!! Try Again!!");
		}
	}

	static void updateCustomer(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		try {
			boolean isUpdated;
			if (session != null) {
				int updatingUserId = (int) session.getAttribute("currentUserId");
				Customer customer = (Customer) request.getAttribute("updatedCustomerObject");
				isUpdated = userController.updateCustomer(customer, updatingUserId);
				if (isUpdated) {
					request.setAttribute("userCreationSuccess", "Customer Updated Successfully!!!");
				} else {
					request.setAttribute("userCreationFailed", "Customer Updation Failed!! Try Again!!");
				}
			}
		} catch (CustomException e) {
			request.setAttribute("userCreationFailed", "Customer Updation Failed!! Try Again!!");
		}
	}

	public static void updatePassword(HttpServletRequest request, HttpServletResponse response) {
		boolean isPasswordUpdated = false;
		int userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("newpassword");
		try {
			isPasswordUpdated = userController.updatePassword(userId, password);
			if (isPasswordUpdated) {
				request.setAttribute("success", "Password Updated Successfully");
			} else {
				request.setAttribute("failed", "Password Updation Failed");
			}
		} catch (CustomException e) {
			request.setAttribute("failed", "Password Updation Failed");
		}
	}

	public static void getEmployeeBranch(HttpServletRequest request, HttpServletResponse response, int userId) {
		try {
			int branchId = userController.getEmployeeBranch(userId);
			if (branchId != 0) {
				request.getSession().setAttribute("employeeBranchId", branchId);
			} else {
				request.setAttribute("error", "A problem occured, Try after sometime");
			}
		} catch (Exception e) {
			request.setAttribute("error", "A problem occured, Try after sometime");
		}
	}

}
