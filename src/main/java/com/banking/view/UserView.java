package com.banking.view;

import java.util.Map;
import java.util.logging.Logger;

import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.User;
import com.banking.utils.CustomException;
import com.banking.utils.DateUtils;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class UserView {
	private static final Logger log = Logger.getLogger(UserView.class.getName());

	public void userViewMessages(String message) {
		log.info(message);
	}

	public void displayInvalidEmployeeId() {
		log.warning("Invalid Employee Id!!!");
	}

	public void displayInvalidDateOfBirth() {
		log.warning("Invalid Date Of Birth!!!");
	}

	public void displayUserProfile(User user) throws CustomException {
		InputValidator.isNull(user, ErrorMessages.INPUT_NULL_MESSAGE);
		log.info("-".repeat(60));
		log.info("User Id : " + user.getUserId());
		log.info("First Name : " + user.getFirstName());
		log.info("Last Name : " + user.getLastName());
		log.info("Gender : " + user.getGender());
		log.info("Email : " + user.getEmail());
		log.info("Contact Number : " + user.getContactNumber());
		log.info("Address : " + user.getAddress());
		log.info("DOB : " + DateUtils.longToDate(user.getDateOfBirth()));
		log.info("Login Account Status : " + user.getStatus());
	}

	public void displayCustomerDetails(Customer customerDetails) throws CustomException {
		InputValidator.isNull(customerDetails, ErrorMessages.INPUT_NULL_MESSAGE);
		displayUserProfile(customerDetails);
		log.info("PAN Number : " + customerDetails.getPanNumber());
		log.info("Aadhar Number : " + customerDetails.getAadharNumber());
		log.info("-".repeat(60));
	}

	public void displayListOfEmployees(Map<Integer, Employee> employeesList) throws CustomException {
		InputValidator.isNull(employeesList, ErrorMessages.INPUT_NULL_MESSAGE);
		if (employeesList.isEmpty()) {
			log.info("No Employee Avaliable!!!");
			return;
		}
		for (Map.Entry<Integer, Employee> entry : employeesList.entrySet()) {
			displayEmployeeProfile(entry.getValue());
		}
	}

	public void displayEmployeesByBranch(Map<Integer, Map<Integer, Employee>> allEmployeesList) throws CustomException {
		for (Map.Entry<Integer, Map<Integer, Employee>> entry : allEmployeesList.entrySet()) {
			int branchId = entry.getKey();
			log.info("Employees From Branch Id : " + branchId);
			displayListOfEmployees(entry.getValue());
		}
	}

	public void displayEmployeeProfile(Employee employeeDetails) throws CustomException {
		InputValidator.isNull(employeeDetails, ErrorMessages.INPUT_NULL_MESSAGE);
		displayUserProfile(employeeDetails);
		log.info("Branch Id : " + employeeDetails.getBranchId());
		log.info("-".repeat(60));
	}

}
