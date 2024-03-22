package com.banking.dao;

import java.util.Map;

import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.User;
import com.banking.utils.CustomException;

public interface UserDao {

	User authendicateUser(int userID, String password) throws CustomException;

	boolean addCustomer(Customer customer) throws CustomException;

	boolean addEmployee(Employee newEmployee) throws CustomException;

	boolean checkUserIdExists(int userId) throws CustomException;

	Customer getCustomerDetails(String accountNumber) throws CustomException;

	boolean checkCustomerIdPresentInBranch(int userId, int branchId) throws CustomException;

	<K extends Enum<K>, V> boolean updateCustomerDetails(int userIdToUpdate, Map<K, V> fieldsToUpdate)
			throws CustomException;

	boolean updatePassword(int userId, String password) throws CustomException;

	boolean checkEmployeeExists(int employeeId) throws CustomException;

	Employee getEmployeeDetails(int employeeId) throws CustomException;

	Map<Integer, Employee> getEmployeesInBranch(int branchId) throws CustomException;

	Map<Integer, Map<Integer, Employee>> getEmployeesFromAllBranch() throws CustomException;

	int getEmployeeBranch(int userId) throws CustomException;

	Customer getCustomerDetailsById(int userId) throws CustomException;
}
