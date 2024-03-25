package com.banking.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.UserType;
import com.banking.utils.DateUtils;
import com.banking.utils.InputValidator;
import com.banking.utils.PasswordGenerator;

public class MainFilter implements Filter {

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
			HttpSession session = ((HttpServletRequest) request).getSession(false);
			boolean flag = false;
			try {
				if (session != null) {
					Boolean isCustomer = (Boolean) session.getAttribute("customer");
					Boolean isEmployee = (Boolean) session.getAttribute("employee");

					String firstName = request.getParameter("firstname");
					String lastName = request.getParameter("lastname");
					String email = request.getParameter("email");
					String gender = request.getParameter("gender");
					String contactNumber = request.getParameter("contactnumber");
					String address = request.getParameter("address");
					String dob = request.getParameter("dateofbirth");
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

						if (flag) {
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

						if (flag) {
							httpRequest.getRequestDispatcher("/employee/customerForm.jsp").forward(httpRequest,
									httpResponse);
							return;
						}

						request.setAttribute("employeeObject", employee);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}

}
