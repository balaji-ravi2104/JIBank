package com.banking.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.controller.AccountController;
import com.banking.dao.UserDao;
import com.banking.dao.implementation.UserDaoImplementation;
import com.banking.model.Account;
import com.banking.model.AccountStatus;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.UserType;
import com.banking.utils.DateUtils;
import com.banking.utils.InputValidator;
import com.banking.utils.PasswordGenerator;

public class MainFilter implements Filter {

	UserDao userDao;
	AccountController accountController;

	public void init(FilterConfig fConfig) throws ServletException {
		this.userDao = new UserDaoImplementation();
		this.accountController = new AccountController();
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String path = httpRequest.getPathInfo();

		System.out.println("In Servlet Filter :" + path);

		switch (path) {
		case "/getCustomer":
			int customerId = Integer.parseInt(request.getParameter("userId"));
			if (customerId <= 1000) {
				request.setAttribute("error", "Invalid Customer Id");
				httpRequest.getRequestDispatcher("/employee/customer.jsp").forward(httpRequest, httpResponse);
				return;
			}
			break;
		case "/addUser":
			try {
				HttpSession session = ((HttpServletRequest) request).getSession(false);
				if (session != null) {
					boolean flag = false;
					Boolean isCustomer = (Boolean) session.getAttribute("customer");
					Boolean isEmployee = (Boolean) session.getAttribute("employee");

					String firstName = request.getParameter("firstname");
					String lastName = request.getParameter("lastname");
					String email = request.getParameter("email");
					String gender = request.getParameter("gender");
					String contactNumber = request.getParameter("contactnumber");
					String address = request.getParameter("address");
					String dob = request.getParameter("dateOfBirth");
					long dateOfBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));

					if (!InputValidator.validateFirstName(firstName)) {
						flag = true;
						request.setAttribute("invalidFirstName", "Invalid FirstName");
					}

					if (!InputValidator.validateLastName(lastName)) {
						flag = true;
						request.setAttribute("invalidLastName", "Invalid LastName");
					}

					if (!InputValidator.validateEmail(email)) {
						flag = true;
						request.setAttribute("invalidEmail", "Invalid Email Id");
					}

					if (!InputValidator.validateMobileNumber(contactNumber)) {
						flag = true;
						request.setAttribute("invalidMobile", "Invalid Contact Number");
					}

					if (dateOfBirth > System.currentTimeMillis()) {
						flag = true;
						request.setAttribute("invalidDOB", "Invalid Date of Birth");
					}

					String date = DateUtils.longToDate(dateOfBirth);
					date = DateUtils.convertToHtmlDateFormat(date);
					request.setAttribute("DOB", date);

					if (isCustomer != null && isCustomer) {
						String panNumber = request.getParameter("pannumber");
						String aadharNumber = request.getParameter("aadharnumber");

						if (!InputValidator.validatePanNumber(panNumber)) {
							flag = true;
							request.setAttribute("invalidPAN", "Invalid PAN Number");
						}

						if (!InputValidator.validateAadharNumber(aadharNumber)) {
							flag = true;
							request.setAttribute("invalidAadhar", "Invalid Aadhar Number");
						}

						if (flag) {
							httpRequest.getRequestDispatcher("/employee/customerform.jsp").forward(httpRequest,
									httpResponse);
							return;
						}

						if (userDao.isCustomerExists(panNumber)) {
							request.setAttribute("customerExists", "Customer Already Exists");
							httpRequest.getRequestDispatcher("/employee/customerform.jsp").forward(httpRequest,
									httpResponse);
							return;
						}

						Customer customer = new Customer();
						customer.setPassword(PasswordGenerator.generatePassword());
						customer.setFirstName(firstName);
						customer.setLastName(lastName);
						customer.setEmail(email);
						customer.setGender(gender);
						customer.setContactNumber(contactNumber);
						customer.setAddress(address);
						customer.setDateOfBirth(dateOfBirth);
						customer.setPanNumber(panNumber);
						customer.setAadharNumber(aadharNumber);
						customer.setTypeOfUser(UserType.CUSTOMER.getValue());

						request.setAttribute("customerObject", customer);

					} else if (isEmployee != null && isEmployee) {
						int branchId = Integer.parseInt(request.getParameter("branchId"));
						if (flag) {
							httpRequest.getRequestDispatcher("/employee/customerform.jsp").forward(httpRequest,
									httpResponse);
							return;
						}
						Employee employee = new Employee();
						employee.setPassword(PasswordGenerator.generatePassword());
						employee.setFirstName(firstName);
						employee.setLastName(lastName);
						employee.setEmail(email);
						employee.setGender(gender);
						employee.setContactNumber(contactNumber);
						employee.setAddress(address);
						employee.setDateOfBirth(dateOfBirth);
						employee.setTypeOfUser(UserType.EMPLOYEE.getValue());
						employee.setBranchId(branchId);
						request.setAttribute("employeeObject", employee);
					}
				}
			} catch (Exception e) {
				request.setAttribute("userCreationFailed", "User Creation Failed!! Try Again!!");
			}
			break;
		case "/updateUser":
			try {
				boolean flag = false;
				customerId = Integer.parseInt(request.getParameter("userId"));
				String firstName = request.getParameter("firstname");
				String lastName = request.getParameter("lastname");
				String email = request.getParameter("email");
				String gender = request.getParameter("gender");
				String contactNumber = request.getParameter("contactnumber");
				String address = request.getParameter("address");
				int status = Integer.parseInt(request.getParameter("status"));
				String dob = request.getParameter("dateOfBirth");

				if (!InputValidator.validateFirstName(firstName)) {
					flag = true;
					request.setAttribute("invalidFirstName", "Invalid FirstName");
				}

				if (!InputValidator.validateLastName(lastName)) {
					flag = true;
					request.setAttribute("invalidLastName", "Invalid LastName");
				}

				if (!InputValidator.validateEmail(email)) {
					flag = true;
					request.setAttribute("invalidEmail", "Invalid Email Id");
				}

				if (!InputValidator.validateMobileNumber(contactNumber)) {
					flag = true;
					request.setAttribute("invalidMobile", "Invalid Contact Number");
				}

				if (!InputValidator.validateDateOfBirth(dob)) {
					flag = true;
					request.setAttribute("invalidDOB", "Invalid Date of Birth");
				}

				long dateOfBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));

				if (dateOfBirth > System.currentTimeMillis()) {
					flag = true;
					request.setAttribute("invalidDOB", "Invalid Date of Birth");
				}

				Customer customer = new Customer();
				customer.setUserId(customerId);
				customer.setFirstName(firstName);
				customer.setLastName(lastName);
				customer.setEmail(email);
				customer.setGender(gender);
				customer.setContactNumber(contactNumber);
				customer.setAddress(address);
				customer.setDateOfBirth(dateOfBirth);
				customer.setStatus(status);

				String date = DateUtils.longToDate(customer.getDateOfBirth());
				date = DateUtils.convertToHtmlDateFormat(date);
				request.setAttribute("DOB", date);

				request.setAttribute("customerDetails", customer);

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/customerform.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("updatedCustomerObject", customer);

			} catch (Exception e) {
				request.setAttribute("userCreationFailed", "Customer Updation Failed!! Try Again!!");
			}
			break;
		case "/account/getAccounts":
			try {
				int userId = Integer.parseInt(request.getParameter("userId"));
				if (userId <= 1000 || !userDao.isValidCustomer(userId)) {
					request.setAttribute("error", "Invalid Customer Id");
					httpRequest.getRequestDispatcher("/employee/account.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("error", "An Error Occured, Try Again");
			}
			break;
		case "/createAccount":
			try {
				boolean flag = false;
				customerId = Integer.parseInt(request.getParameter("userId"));
				int branchId = Integer.parseInt(request.getParameter("branchId"));
				int accountType = Integer.parseInt(request.getParameter("accountType"));
				double balance = Double.parseDouble(request.getParameter("balance"));

				if (customerId <= 1000 || !userDao.isValidCustomer(customerId)) {
					flag = true;
					request.setAttribute("error", "Invalid Customer Id");
				}

				if (InputValidator.validateBalance(balance)) {
					flag = true;
					request.setAttribute("invalidBalance", "Balance Should be Greater than Zero");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/accountform.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (accountController.isUserAlreadyHasAccount(customerId, accountType, branchId)) {
					request.setAttribute("accountExists", "Account Already Present in the Branch");
					httpRequest.getRequestDispatcher("/employee/accountform.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account account = new Account();
				account.setUserId(customerId);
				account.setBranchId(branchId);
				account.setAccountType(accountType);
				account.setBalance(balance);

				request.setAttribute("accountObject", account);

			} catch (Exception e) {
				request.setAttribute("failure", "Account Creation Failed");
			}
			break;
		case "/getTransactions":
			try {
				String accountNumber = request.getParameter("accountNumber");
				String fromDate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");

				if (accountNumber.length() < 12 || accountNumber.length() > 12
						|| !accountController.isAccountPresent(accountNumber)) {
					request.setAttribute("error", "Invalid Account Number");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date fromDateObj = sdf.parse(fromDate);
				Date toDateObj = sdf.parse(toDate);

				if (fromDateObj.after(toDateObj)) {
					request.setAttribute("dateError", "From date should be less then To date");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("message", "An Error Occured, Try Again");
			}
			break;
		case "/employeeDeposit":
			try {
				String accountNumber = request.getParameter("transactionaccountNumber");
				double amount = Double.parseDouble(request.getParameter("amount"));
				request.setAttribute("transactionType", "Deposit Amount");
				request.setAttribute("submitType", "Deposit");
				boolean flag = false;
				if (accountNumber.length() < 12 || accountNumber.length() > 12
						|| !accountController.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Balance Should be Greater than Zero");
				}

				if (amount < 100 || amount > 100000) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be greater than 100 and less than 100000");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account Account = accountController.getAccountDetails(accountNumber);

				if (Account.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "The Account is InActive");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("account", Account);
			} catch (Exception e) {
				request.setAttribute("failed", "Amount Deposit Failed");
			}
			break;
		case "/employeeWithdraw":
			try {
				String accountNumber = request.getParameter("transactionaccountNumber");
				double amount = Double.parseDouble(request.getParameter("amount"));
				request.setAttribute("transactionType", "Withdraw Amount");
				request.setAttribute("submitType", "Withdraw");
				boolean flag = false;
				if (accountNumber.length() < 12 || accountNumber.length() > 12
						|| !accountController.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than Zero");
				}

				if (amount < 100 || amount > 100000) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be greater than 100 and less than 100000");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account Account = accountController.getAccountDetails(accountNumber);

				if (Account.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "The Account is InActive");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (Account.getBalance() < amount) {
					request.setAttribute("inactiveAccount", "Insufficient Balance");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("account", Account);
			} catch (Exception e) {
				request.setAttribute("failed", "Amount Withdraw Failed");
			}
			break;
		case "/withinBankTransfer":
			try {
				String accountNumber = request.getParameter("accountNumber");
				double amount = Double.parseDouble(request.getParameter("amount"));
				// int branchId = Integer.parseInt(request.getParameter("branchId"));
				boolean flag = false;
				HttpSession session = ((HttpServletRequest) request).getSession(false);

				Account senderAccount = (Account) session.getAttribute("currentAccount");

				if (senderAccount == null) {
					request.setAttribute("inactiveAccount", "You don't have Any Accounts");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (senderAccount.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "Your Account is InActive");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (accountNumber.length() < 12 || accountNumber.length() > 12
						|| !accountController.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than Zero");
				}
				if (amount < 100 || amount > 100000) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than 100 and Less than 100000");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account receiverAccount = accountController.getAccountDetails(accountNumber);

				if (receiverAccount.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "Receiver Account is InActive");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (senderAccount.getBalance() < amount) {
					request.setAttribute("inactiveAccount", "Insufficent Balance !! Can't Transfer");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
					request.setAttribute("inactiveAccount", "Self Account Transfer Not Allowed");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("receiverAccount", receiverAccount);
				request.setAttribute("senderAccount", senderAccount);
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("failed", "Amount Transaction Failed");
			}
			break;
		case "/otherBankTransfer":
			try {
				String accountNumber = request.getParameter("accountNumber");
				double amount = Double.parseDouble(request.getParameter("amount"));
				boolean flag = false;
				HttpSession session = ((HttpServletRequest) request).getSession(false);

				Account senderAccount = (Account) session.getAttribute("currentAccount");

				if (senderAccount == null) {
					request.setAttribute("inactiveAccount", "You don't have Any Accounts");
					httpRequest.getRequestDispatcher("/transferOutSideBank").forward(httpRequest, httpResponse);
					return;
				}

				if (senderAccount.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "Your Account is InActive");
					httpRequest.getRequestDispatcher("/transferOutSideBank").forward(httpRequest, httpResponse);
					return;
				}

				if (accountNumber.length() < 12) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than Zero");
				}

				if (amount < 100 || amount > 100000) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be greater than 100 and Less than 100000");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/transferOutSideBank").forward(httpRequest, httpResponse);
					return;
				}

				if (senderAccount.getBalance() < amount) {
					request.setAttribute("inactiveAccount", "Insufficent Balance !! Can't Transfer");
					httpRequest.getRequestDispatcher("/transferOutSideBank").forward(httpRequest, httpResponse);
					return;
				}
				request.setAttribute("senderAccount", senderAccount);
			} catch (Exception e) {
				request.setAttribute("failed", "Amount Transaction Failed");
			}
			break;
		case "/getStatements":
			try {
				String fromDate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date fromDateObj = sdf.parse(fromDate);
				Date toDateObj = sdf.parse(toDate);
				HttpSession session = ((HttpServletRequest) request).getSession(false);

				Account senderAccount = (Account) session.getAttribute("currentAccount");

				if (senderAccount == null) {
					request.setAttribute("message", "You don't have Any Accounts");
					httpRequest.getRequestDispatcher("/customer/statement.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (fromDateObj.after(toDateObj)) {
					request.setAttribute("dateError", "From date should be less then To date");
					httpRequest.getRequestDispatcher("/customer/statement.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("message", "An Error Occured, Try Again");
			}
			break;
		case "/updatePassword":
			try {
				String oldPassword = request.getParameter("oldpassword");
				String newPassword = request.getParameter("newpassword");
				String confirmPassword = request.getParameter("confirmpassword");
				int userId = Integer.parseInt(request.getParameter("userId"));
				String password = userDao.getUserPassword(userId);

				if (!password.equals(oldPassword)) {
					request.setAttribute("wrongPassword", "Wrong Password");
					httpRequest.getRequestDispatcher("/customer/profile.jsp").forward(httpRequest, httpResponse);
					return;
				}
				if (newPassword.length() < 8 || !InputValidator.validatePassword(newPassword)) {
					request.setAttribute("InvalidPassword",
							"Password must contain at least 8 characters, including at least one lowercase letter, one uppercase letter, one digit, and one special character");
					httpRequest.getRequestDispatcher("/customer/profile.jsp").forward(httpRequest, httpResponse);
					return;
				}
				if (!newPassword.equals(confirmPassword)) {
					request.setAttribute("diffPassword", "New and Confirm Password Must be Same");
					httpRequest.getRequestDispatcher("/customer/profile.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("failed", "Password Updation Failed");
			}
			break;
		case "/api/getapikey":
			int userId = Integer.parseInt(request.getParameter("userId"));
			try {
				if (userId <= 1000 || !userDao.checkEmployeeExists(userId)) {
					request.setAttribute("error", "Invalid User Id");
					httpRequest.getRequestDispatcher("/employee/apiservice.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("error", "A problem occured, Try after sometime");
			}
			break;
		case "/api/createapikey":
			userId = Integer.parseInt(request.getParameter("userId"));
			try {
				if (userId <= 1000 || !userDao.checkEmployeeExists(userId)) {
					request.setAttribute("error", "Invalid User Id");
					httpRequest.getRequestDispatcher("/employee/apiservice.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				request.setAttribute("error", "A problem occured, Try after sometime");
			}
			break;
		}
		chain.doFilter(request, response);
	}
}
