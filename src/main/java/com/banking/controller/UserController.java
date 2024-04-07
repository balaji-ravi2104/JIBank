package com.banking.controller;

import java.util.Map;
import java.util.logging.Logger;

import com.banking.cache.Cache;
import com.banking.cache.RedisCache;
import com.banking.dao.UserDao;
import com.banking.dao.implementation.UserDaoImplementation;
import com.banking.logservice.AuditLogHandler;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.SessionDetails;
import com.banking.model.User;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.view.UserView;

public class UserController {

	private static final Logger log = Logger.getLogger(MainController.class.getName());
	private UserDao userDao = new UserDaoImplementation();;
	private BranchController branchController = new BranchController();
	private AuditLogHandler auditLogHandler = new AuditLogHandler();
	private UserView userView = new UserView();
	private AccountController accountController;
	public static final String cachePrefix = "Customer";

	private static final Object userCacheLock = new Object();

	// public static final Cache<Integer, Customer> userCache = new LRUCache<>(50);
	public static final Cache<Integer, Customer> userCache = new RedisCache<Integer, Customer>(6379, cachePrefix);

	public UserController(AccountController accountController) {
		this.accountController = accountController;
	}

	public UserController() {
	}

	public User login(int userId, String password) throws CustomException {
		InputValidator.isNull(password, "Password Cannot be Empty or Null!!!");
		User user = null;
		try {
			if (isUserExists(userId)) {
				String userPassword = userDao.getUserPassword(userId);
				if (!userPassword.equals(password)) {
					return user;
				}
			}else {
				return user;
			}
			user = userDao.authendicateUser(userId);
		} catch (Exception e) {
			throw new CustomException("Error while loggin!!", e);
		}
		return user;
	}

	public boolean registerNewCustomer(Customer customer,int creatingUserId) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isRegistred = false;
		try {
			isRegistred = userDao.addCustomer(customer,creatingUserId);
		} catch (Exception e) {
			throw new CustomException("Error while creating new User!!", e);
		}
		return isRegistred;
	}

	public boolean registerNewEmployee(Employee newEmployee,int creatingUserId) throws CustomException {
		InputValidator.isNull(newEmployee, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isRegistred = false;
		try {
			isRegistred = userDao.addEmployee(newEmployee,creatingUserId);
		} catch (Exception e) {
			throw new CustomException("Error while creating new User!!", e);
		}
		return isRegistred;
	}

	public boolean isUserExists(int userId) throws CustomException {
		try {
			return userDao.checkUserIdExists(userId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking User Exists!!", e);
		}
	}

	public int getEmployeeBranch(int userId) throws CustomException {
		try {
			return userDao.getEmployeeBranch(userId);
		} catch (Exception e) {
			throw new CustomException("Error while Getting Employee Branch!!", e);
		}
	}

	public Customer getCustomerDetails(String accountNumber, int branchId) throws CustomException {
		InputValidator.isNull(accountNumber, "Account Number Cannot be Empty or Null!!!");
		Customer customerDetails = null;
		if (!accountController.validateAccountAndBranch(accountNumber, branchId)) {
			return customerDetails;
		}
		try {
			customerDetails = userDao.getCustomerDetails(accountNumber);
		} catch (Exception e) {
			throw new CustomException("Error while Getting User Exists!!", e);
		}
		return customerDetails;
	}

	// For Employee Purpose
	public Customer getCustomerDetailsById(int userId, int branchId) throws CustomException {
		Customer customerDetails = null;
		if (!validateUserIdAndBranchId(userId, branchId)) {
			return customerDetails;
		}
		synchronized (userCacheLock) {
			if (userCache.get(cachePrefix + userId) != null) {
				System.out.println("Inside Cache(Employee Purpose) User Id : " + userId);
				return userCache.get(cachePrefix + userId);
			}
			try {
				customerDetails = userDao.getCustomerDetailsById(userId);
				if (customerDetails != null) {
					userCache.set(userId, customerDetails);
				}
			} catch (Exception e) {
				throw new CustomException("Error while Getting Customer Details!!", e);
			}
		}
		return customerDetails;
	}

	// For Admin Purpose
	public Customer getCustomerDetailsById(int userId) throws CustomException {
		Customer customerDetails = null;
		synchronized (userCacheLock) {
			if (userCache.get(cachePrefix + userId) != null) {
				System.out.println("Inside Cache(Admin Purpose) User Id : " + userId);
				return userCache.get(cachePrefix + userId);
			}
			try {
				customerDetails = userDao.getCustomerDetailsById(userId);
				if (customerDetails != null) {
					userCache.set(userId, customerDetails);
				}
			} catch (Exception e) {
				throw new CustomException("Error while Getting Customer Details!!", e);
			}
		}
		return customerDetails;
	}

	public <K extends Enum<K>, V> boolean updateCustomer(int userIdToUpdate, Map<K, V> fieldsToUpdate)
			throws CustomException {
		InputValidator.isNull(fieldsToUpdate, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isCustomerUpdated = false;
		try {
			isCustomerUpdated = userDao.updateCustomerDetails(userIdToUpdate, fieldsToUpdate);
		} catch (Exception e) {
			throw new CustomException("Error while Updating User", e);
		}
		return isCustomerUpdated;
	}

	public boolean isUserExistsInTheBranch(int userId, int branchId) throws CustomException {
		boolean isExists = false;
		try {
			isExists = userDao.checkCustomerIdPresentInBranch(userId, branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking User Exists!!", e);
		}
		return isExists;
	}

	public boolean updateCustomer(Customer customer,int updatingUserId) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isUpdated = false;
		if (userCache.get(cachePrefix + customer.getUserId()) != null) {
			userCache.rem(cachePrefix + customer.getUserId());
		}
		try {
			isUpdated = userDao.updateCustomerDetails(customer,updatingUserId);
		} catch (Exception e) {
			throw new CustomException("Error while Updation Customer Details", e);
		}
		return isUpdated;
	}

	public boolean updatePassword(int userId, String password) throws CustomException {
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isPasswordUpdated = false;
		try {
			isPasswordUpdated = userDao.updatePassword(userId, password);
		} catch (Exception e) {
			throw new CustomException("Error while Updating Password!!", e);
		}
		return isPasswordUpdated;
	}

	public Employee getEmployeeDetails(int employeeId) throws CustomException {
		Employee employee = null;
		if (!isEmployeeExists(employeeId)) {
			userView.displayInvalidEmployeeId();
			return employee;
		}
		try {
			employee = userDao.getEmployeeDetails(employeeId);
		} catch (Exception e) {
			throw new CustomException("Error while Reterving Employee Details!!", e);
		}
		return employee;
	}

	public Map<Integer, Employee> getEmployeeFromOneBranch(int branchId) throws CustomException {
		Map<Integer, Employee> allEmployee = null;
		if (!branchController.validateBranchId(branchId)) {
			return allEmployee;
		}
		try {
			allEmployee = userDao.getEmployeesInBranch(branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Reterving Employee Details!!", e);
		}
		return allEmployee;
	}

	public Map<Integer, Map<Integer, Employee>> getEmployeeFromAllBranch() throws CustomException {
		try {
			return userDao.getEmployeesFromAllBranch();
		} catch (Exception e) {
			throw new CustomException("Error while Reterving Employee Details!!", e);
		}
	}


	public boolean validateUserIdAndBranchId(int userId, int branchId) throws CustomException {
		boolean isValidId = isUserExistsInTheBranch(userId, branchId);
		if (!isValidId) {
			log.warning("UserID is Not present in this Branch!!");
		}
		return isValidId;
	}

	private boolean isEmployeeExists(int employeeId) throws CustomException {
		boolean isExixts = false;
		try {
			isExixts = userDao.checkEmployeeExists(employeeId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking Employee Exists!!", e);
		}
		return isExixts;
	}

	public boolean logSessionData(SessionDetails sessionDetails) throws CustomException {
		InputValidator.isNull(sessionDetails, "Session object cannot be Null");
		boolean isSessionLogged = false;
		try {
			isSessionLogged = auditLogHandler.logLoginSession(sessionDetails);
		}catch (Exception e) {
			throw new CustomException("Error while logging session details", e);
		}
		return isSessionLogged;
	}

	public boolean updateLogoutSession(String sessionId, int userId) throws CustomException {
		boolean isSessionUpdated = false;
		try {
			isSessionUpdated = auditLogHandler.updateLogoutSession(sessionId,userId);
		}catch (Exception e) {
			throw new CustomException("Error while updating logging session details", e);
		}
		return isSessionUpdated;
	}
}
