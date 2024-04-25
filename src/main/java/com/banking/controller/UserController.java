package com.banking.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.cache.Cache;
import com.banking.cache.RedisCache;
import com.banking.dao.UserDao;
import com.banking.dao.implementation.UserDaoImplementation;
import com.banking.logservice.AuditLogHandler;
import com.banking.model.AuditLog;
import com.banking.model.AuditlogActions;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.Status;
import com.banking.model.User;
import com.banking.utils.CustomException;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;
import com.banking.view.UserView;

public class UserController {

	private UserDao userDao;
	private BranchController branchController;
	private AuditLogHandler auditLogHandler;
	private UserView userView;
	public static final String cachePrefix = "Customer";

	private static final Object userCacheLock = new Object();
	public static final Cache<Integer, Customer> userCache = new RedisCache<Integer, Customer>(6379, cachePrefix);
	
	private static final Logger logger = LoggerProvider.getLogger();

	public UserController() {
		this.userDao = new UserDaoImplementation();
		this.branchController = new BranchController();
		this.auditLogHandler = new AuditLogHandler();
		this.userView = new UserView();
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
			logger.log(Level.WARNING,"Exception Occured While Login",e);
			throw new CustomException("Exception Occured While loggin!!", e);
		}
		return null;
	}

	public int registerNewCustomer(Customer customer, int creatingUserId) throws CustomException { //used
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		int customerId = 0;
		try {
			customerId = userDao.addCustomer(customer, creatingUserId);
			if (customerId != 0) {
				AuditLog auditLog = new AuditLog(customerId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Created New Customer With Id of %d", creatingUserId, customerId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(customerId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Try to Create New Customer but Failed", creatingUserId),
						Status.FAILURE);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Creating new Customer",e);
			throw new CustomException("Exception Occured While Creating new Customer", e);
		}
		return customerId;
	}

	public int registerNewEmployee(Employee newEmployee, int creatingUserId) throws CustomException {  
		InputValidator.isNull(newEmployee, ErrorMessages.INPUT_NULL_MESSAGE);
		int employeeId = 0;
		try {
			employeeId = userDao.addEmployee(newEmployee, creatingUserId);
			if (employeeId != 0) {
				AuditLog auditLog = new AuditLog(employeeId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Created New Employee With Id of %d", creatingUserId, employeeId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(employeeId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Try to Create New Employee but Failed", creatingUserId),
						Status.FAILURE);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Creating new Employee",e);
			throw new CustomException("Exception Occured While Creating new Employee", e);
		}
		return employeeId;
	}

	public int getEmployeeBranch(int userId) throws CustomException {  
		try {
			return userDao.getEmployeeBranch(userId);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Getting Employee Branch",e);
			throw new CustomException("Exception Occured While Getting Employee Branch", e);
		}
	}

	public Customer getCustomerDetails(String accountNumber, int branchId) throws CustomException {
		InputValidator.isNull(accountNumber, "Account Number Cannot be Empty or Null!!!");
		Customer customerDetails = null;
		try {
			customerDetails = userDao.getCustomerDetails(accountNumber);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Getting Customer Details",e);
			throw new CustomException("Exception Occured While Getting Customer Details", e);
		}
		return customerDetails;
	}
	
	
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
				logger.log(Level.WARNING,"Exception Occured While Reterving Customer Details",e);
				throw new CustomException("Exception Occured While Reterving Customer Details", e);
			}
		}
		return customerDetails;
	}

	public boolean isUserExistsInTheBranch(int userId, int branchId) throws CustomException {
		boolean isExists = false;
		try {
			isExists = userDao.checkCustomerIdPresentInBranch(userId, branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Customer Exists In Branch",e);
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
			if (isUpdated) {
				AuditLog auditLog = new AuditLog(customer.getUserId(), AuditlogActions.UPDATE,
						System.currentTimeMillis(), updatingUserId,
						String.format("User id %d Updates the Details of User Id %d", updatingUserId,
								customer.getUserId()),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(customer.getUserId(), AuditlogActions.UPDATE,
						System.currentTimeMillis(), updatingUserId,
						String.format("User id %d Try to Updates the Details of User Id %d But Failed", updatingUserId,
								customer.getUserId()),
						Status.FAILURE);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Updating Customer Details",e);
			throw new CustomException("Exception Occured While Updating Customer Details", e);
		}
		return isUpdated;
	}

	public boolean updatePassword(int userId, String password) throws CustomException {  
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isPasswordUpdated = false;
		try {
			isPasswordUpdated = userDao.updatePassword(userId, password);
			if (isPasswordUpdated) {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(), userId,
						String.format("User id %d Updated the Account login Password", userId), Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			} else {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(), userId,
						String.format("User id %d Try to Updated the Account login Password but Failed", userId), Status.FAILURE);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Updating Password",e);
			throw new CustomException("Exception Occured While Updating Password", e);
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
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return employee;
	}

	public Map<Integer, Employee> getEmployeeFromOneBranch(int branchId) throws CustomException {
		Map<Integer, Employee> allEmployee = null;
		if (!branchController.isBranchExists(branchId)) {
			return allEmployee;
		}
		try {
			allEmployee = userDao.getEmployeesInBranch(branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return allEmployee;
	}

	public Map<Integer, Map<Integer, Employee>> getEmployeeFromAllBranch() throws CustomException {
		try {
			return userDao.getEmployeesFromAllBranch();
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
	}

	private boolean isEmployeeExists(int employeeId) throws CustomException {
		boolean isExixts = false;
		try {
			isExixts = userDao.checkEmployeeExists(employeeId);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Employee Details",e);
			throw new CustomException("Exception Occured While Checking Employee Details", e);
		}
		return isExixts;
	}
}
