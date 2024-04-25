package com.banking.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.cache.Cache;
import com.banking.cache.RedisCache;
import com.banking.dao.UserDao;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.Status;
import com.banking.model.User;
import com.banking.utils.AuditLogUtils;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class UserController {

	private UserDao userDao;
	public static final String cachePrefix = "Customer";

	public static final Cache<Integer, Customer> userCache = new RedisCache<Integer, Customer>(6379, cachePrefix);

	private static final Logger logger = LoggerProvider.getLogger();

	public UserController() {
		try {
			Class<?> clazz = Class.forName("com.banking.dao.implementation.UserDaoImplementation");
			this.userDao = (UserDao) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public User login(int userId, String password) throws CustomException {
		InputValidator.isNull(password, "Password Cannot be Empty or Null!!!");
		try {
			User user = userDao.authendicateUser(userId);
			if (user != null && user.getPassword().equals(password)) {
				user.setPassword(null);
				return user;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Login", e);
			throw new CustomException("Exception Occured While loggin", e);
		}
		return null;
	}

	public int registerNewCustomer(Customer customer, int creatingUserId) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		int customerId = 0;
		try {
			customerId = userDao.addCustomer(customer, creatingUserId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Creating new Customer", e);
			throw new CustomException("Exception Occured While Creating new Customer", e);
		} finally {
			AuditLogUtils.logUserCreation(customerId, creatingUserId,
					customerId != 0 ? Status.SUCCESS : Status.FAILURE);
		}
		return customerId;
	}

	public int registerNewEmployee(Employee employee, int creatingUserId) throws CustomException {
		InputValidator.isNull(employee, ErrorMessages.INPUT_NULL_MESSAGE);
		int employeeId = 0;
		try {
			employeeId = userDao.addEmployee(employee, creatingUserId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Creating new Employee", e);
			throw new CustomException("Exception Occured While Creating new Employee", e);
		} finally {
			AuditLogUtils.logUserCreation(employeeId, creatingUserId,
					employeeId != 0 ? Status.SUCCESS : Status.FAILURE);
		}
		return employeeId;
	}

	public int getEmployeeBranch(int userId) throws CustomException {
		try {
			return userDao.getEmployeeBranch(userId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Getting Employee Branch", e);
			throw new CustomException("Exception Occured While Getting Employee Branch", e);
		}
	}

	public Customer getCustomerDetails(String accountNumber, int branchId) throws CustomException {
		InputValidator.isNull(accountNumber, "Account Number Cannot be Empty or Null!!!");
		Customer customerDetails = null;
		try {
			customerDetails = userDao.getCustomerDetails(accountNumber);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Getting Customer Details", e);
			throw new CustomException("Exception Occured While Getting Customer Details", e);
		}
		return customerDetails;
	}

	public Customer getCustomerDetailsById(int userId) throws CustomException {
		Customer customerDetails = null;
		if (userCache.get(cachePrefix + userId) != null) {
			return userCache.get(cachePrefix + userId);
		}
		try {
			customerDetails = userDao.getCustomerDetailsById(userId);
			if (customerDetails != null) {
				userCache.set(userId, customerDetails);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Customer Details", e);
			throw new CustomException("Exception Occured While Reterving Customer Details", e);
		}
		return customerDetails;
	}

	public boolean isUserExistsInTheBranch(int userId, int branchId) throws CustomException {
		boolean isExists = false;
		try {
			isExists = userDao.checkCustomerIdPresentInBranch(userId, branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Checking Customer Exists In Branch", e);
			throw new CustomException("Exception Occured While Checking Customer Exists In Branch", e);
		}
		return isExists;
	}

	public boolean updateCustomer(Customer customer, int updatingUserId) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isUpdated = false;
		if (userCache.get(cachePrefix + customer.getUserId()) != null) {
			userCache.rem(cachePrefix + customer.getUserId());
		}
		try {
			isUpdated = userDao.updateCustomerDetails(customer, updatingUserId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Updating Customer Details", e);
			throw new CustomException("Exception Occured While Updating Customer Details", e);
		} finally {
			AuditLogUtils.logUserUpdation(customer.getUserId(), updatingUserId,
					isUpdated ? Status.SUCCESS : Status.FAILURE);
		}
		return isUpdated;
	}

	public boolean updatePassword(int userId, String password) throws CustomException {
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isPasswordUpdated = false;
		try {
			isPasswordUpdated = userDao.updatePassword(userId, password);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Updating Password", e);
			throw new CustomException("Exception Occured While Updating Password", e);
		} finally {
			AuditLogUtils.logPasswordUpdation(userId, isPasswordUpdated ? Status.SUCCESS : Status.FAILURE);
		}
		return isPasswordUpdated;
	}

	public Employee getEmployeeDetails(int employeeId) throws CustomException {
		Employee employee = null;
		try {
			employee = userDao.getEmployeeDetails(employeeId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Employee Details", e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return employee;
	}

	public Map<Integer, Employee> getEmployeeFromOneBranch(int branchId) throws CustomException {
		Map<Integer, Employee> allEmployee = null;
		try {
			allEmployee = userDao.getEmployeesInBranch(branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Employee Details", e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return allEmployee;
	}

	public Map<Integer, Map<Integer, Employee>> getEmployeeFromAllBranch() throws CustomException {
		try {
			return userDao.getEmployeesFromAllBranch();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Employee Details", e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
	}

}
