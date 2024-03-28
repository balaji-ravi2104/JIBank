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

import com.banking.dao.AccountDao;
import com.banking.dao.UserDao;
import com.banking.dao.implementation.AccountDaoImplementation;
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
	AccountDao accountDao;

	public void init(FilterConfig fConfig) throws ServletException {
		this.userDao = new UserDaoImplementation();
		this.accountDao = new AccountDaoImplementation();
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURI = httpRequest.getRequestURI();

		System.out.println(requestURI);

		switch (requestURI) {
		case "/JIBank/login":
			int userId = Integer.parseInt(request.getParameter("userId"));
			if (userId <= 1000) {
				request.setAttribute("error", "Invalid User Id !!!");
				httpRequest.getRequestDispatcher("/login.jsp").forward(httpRequest, httpResponse);
				return;
			}
			break;
		case "/JIBank/getcustomer":
			int customerId = Integer.parseInt(request.getParameter("userId"));
			if (customerId <= 1000) {
				request.setAttribute("error", "Invalid Customer Id");
				httpRequest.getRequestDispatcher("/employee/customer.jsp").forward(httpRequest, httpResponse);
				return;
			}
			break;
		case "/JIBank/addUser":
			try {
				HttpSession session = ((HttpServletRequest) request).getSession(false);
				boolean flag = false;
				if (session != null) {
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

					if (!InputValidator.validateEmail(email)) {
						flag = true;
						request.setAttribute("invalidEmail", "Invalid Email Id");
					}

					if (!InputValidator.validateMobileNumber(contactNumber)) {
						flag = true;
						request.setAttribute("invalidMobile", "Invalid Contact Number");
					}

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

						String date = DateUtils.longToDate(dateOfBirth);
						date = DateUtils.convertToHtmlDateFormat(date);
						request.setAttribute("DOB", date);

						if (flag) {
							httpRequest.getRequestDispatcher("/employee/customerForm.jsp").forward(httpRequest,
									httpResponse);
							return;
						}

						if (userDao.isCustomerExists(panNumber)) {
							request.setAttribute("customerExists", "Customer Already Exists");
							httpRequest.getRequestDispatcher("/employee/customerForm.jsp").forward(httpRequest,
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
							httpRequest.getRequestDispatcher("/employee/customerForm.jsp").forward(httpRequest,
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
				e.printStackTrace();
			}
			break;
		case "/JIBank/updateUser":
			boolean flag = false;
			try {
				customerId = Integer.parseInt(request.getParameter("userId"));
				String firstName = request.getParameter("firstname");
				String lastName = request.getParameter("lastname");
				String email = request.getParameter("email");
				String gender = request.getParameter("gender");
				String contactNumber = request.getParameter("contactnumber");
				String address = request.getParameter("address");
				int status = Integer.parseInt(request.getParameter("status"));
				String dob = request.getParameter("dateOfBirth");
				long dateOfBirth = DateUtils.formatDate(DateUtils.formatDateString(dob));

				if (!InputValidator.validateEmail(email)) {
					flag = true;
					request.setAttribute("invalidEmail", "Invalid Email Id");
				}

				if (!InputValidator.validateMobileNumber(contactNumber)) {
					flag = true;
					request.setAttribute("invalidMobile", "Invalid Contact Number");
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
					httpRequest.getRequestDispatcher("/employee/customerForm.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("updatedCustomerObject", customer);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/getAccounts":
			userId = Integer.parseInt(request.getParameter("userId"));
			try {
				if (userId <= 1000 || !userDao.checkUserIdExists(userId)) {
					request.setAttribute("error", "Invalid Customer Id");
					httpRequest.getRequestDispatcher("/employee/account.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/createAccount":
			flag = false;
			try {
				customerId = Integer.parseInt(request.getParameter("userId"));
				int branchId = Integer.parseInt(request.getParameter("branchId"));
				int accountType = Integer.parseInt(request.getParameter("accountType"));
				double balance = Double.parseDouble(request.getParameter("balance"));

				if (customerId <= 1000 || !userDao.checkUserIdExists(customerId)) {
					flag = true;
					request.setAttribute("error", "Invalid Customer Id");
				}

				if (InputValidator.validateBalance(balance)) {
					flag = true;
					request.setAttribute("invalidBalance", "Balance Should be Greater than Zero");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/accountForm.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (accountDao.isCustomerAlreadyHasAccount(customerId, accountType, branchId)) {
					request.setAttribute("accountExists", "Account Already Present in the Branch");
					httpRequest.getRequestDispatcher("/employee/accountForm.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account account = new Account();
				account.setUserId(customerId);
				account.setBranchId(branchId);
				account.setAccountType(accountType);
				account.setBalance(balance);

				request.setAttribute("accountObject", account);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/getTransactions":
			String accountNumber = request.getParameter("accountNumber");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			long accountNum = Long.parseLong(accountNumber);

			try {
				if (accountNumber.length() < 12 || accountNum <= 0 || !accountDao.isAccountPresent(accountNumber)) {
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
				e.printStackTrace();
			}
			break;
		case "/JIBank/employeeDeposit":
			accountNumber = request.getParameter("transactionaccountNumber");
			double amount = Double.parseDouble(request.getParameter("amount"));
			request.setAttribute("transactionType", "Deposit Amount");
			request.setAttribute("submitType", "Deposit");
			flag = false;
			try {
				if (accountNumber.length() < 12 || !accountDao.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Balance Should be Greater than Zero");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account account = accountDao.getAccountDetail(accountNumber);

				if (account.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "The Account is InActive");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("account", account);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/employeeWithdraw":
			accountNumber = request.getParameter("transactionaccountNumber");
			amount = Double.parseDouble(request.getParameter("amount"));
			request.setAttribute("transactionType", "Withdraw Amount");
			request.setAttribute("submitType", "Withdraw");
			flag = false;
			try {
				if (accountNumber.length() < 12 || !accountDao.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than Zero");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account account = accountDao.getAccountDetail(accountNumber);

				if (account.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "The Account is InActive");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (account.getBalance() < amount) {
					request.setAttribute("inactiveAccount", "Insufficient Balance");
					httpRequest.getRequestDispatcher("/employee/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				request.setAttribute("account", account);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/customer/transaction.jsp":
			request.setAttribute("withinBank", ((HttpServletRequest) request).getContextPath() + "/withinBankTransfer");
			break;
		case "/JIBank/withinBankTransfer":
			accountNumber = request.getParameter("accountNumber");
			amount = Double.parseDouble(request.getParameter("amount"));
			int branchId = Integer.parseInt(request.getParameter("branchId"));
			flag = false;
			try {
				HttpSession session = ((HttpServletRequest) request).getSession(false);

				Account senderAccount = (Account) session.getAttribute("currentAccount");

				if (senderAccount.getAccountStatus() == AccountStatus.INACTIVE) {
					request.setAttribute("inactiveAccount", "Your Account is InActive");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				if (accountNumber.length() < 12 || accountNumber.length() > 12
						|| !accountDao.isAccountPresent(accountNumber)) {
					flag = true;
					request.setAttribute("invalidAccount", "Invalid Account Number");
				}
				if (InputValidator.validateBalance(amount)) {
					flag = true;
					request.setAttribute("invalidBalance", "Amount Should be Greater than Zero");
				}

				if (flag) {
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}

				Account receiverAccount = accountDao.getAccountDetail(accountNumber);

				boolean isAccountPresent = accountDao.checkAccountExists(accountNumber, branchId);

				if (!isAccountPresent) {
					request.setAttribute("inactiveAccount", "Receiver Account not present in this Branch");
					httpRequest.getRequestDispatcher("/customer/transaction.jsp").forward(httpRequest, httpResponse);
					return;
				}
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

				request.setAttribute("receiverAccount", receiverAccount);
				request.setAttribute("senderAccount", senderAccount);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "/JIBank/otherBankTransfer":
			accountNumber = request.getParameter("accountNumber");
			amount = Double.parseDouble(request.getParameter("amount"));
			flag = false;
			try {
				HttpSession session = ((HttpServletRequest) request).getSession(false);

				Account senderAccount = (Account) session.getAttribute("currentAccount");

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
				e.printStackTrace();
			}
			break;
		case "/JIBank/getStatements":
			System.out.println("getStatements inside");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date fromDateObj = sdf.parse(fromDate);
				Date toDateObj = sdf.parse(toDate);

				if (fromDateObj.after(toDateObj)) {
					request.setAttribute("dateError", "From date should be less then To date");
					httpRequest.getRequestDispatcher("customer/Statement.jsp").forward(httpRequest, httpResponse);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		default:
			break;
		}
		chain.doFilter(request, response);
	}
}
