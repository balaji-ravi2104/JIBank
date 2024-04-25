package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.UserDao;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.User;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class UserDaoImplementation implements UserDao {
	
	private static final Logger logger = LoggerProvider.getLogger();

	private static final String GET_USER = "SELECT * FROM Users u WHERE u.userId = ?";

	private static final String GET_EMPLOYEE_BRANCH = "SELECT branch_id FROM Employee WHERE User_id = ?";

	private static final String CREATE_EMPLOYEE = "INSERT INTO Employee (User_id,branch_id) Values (?,?);";

	private static final String CREATE_NEW_USER = "INSERT INTO Users (Password, FirstName, LastName, Gender, Email, "
			+ "ContactNumber, Address, DateOfBirth, TypeId,CreatedBy,ModifiedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);";

	private static final String CREATE_CUSTOMER = "INSERT INTO Customer (User_id, Pan, Aadhar) VALUES (?, ?, ?);";

	private static final String CHECK_CUSTOMER_ID = "SELECT u.UserId FROM Users u WHERE u.UserId = ? and u.TypeId=1;";

	private static final String CHECK_CUSTOMER_ID_EXISTS_QUERY_IN_BRANCH = "SELECT COUNT(*) FROM Users u JOIN "
			+ "Accounts a ON u.UserId = a.user_id WHERE u.UserId = ? AND a.branch_id = ? AND u.TypeId = 1;";

	private static final String GET_CUSTOMER_DETAIL_BY_ACCOUNT_NUMBER = "SELECT u.UserId, u.FirstName, u.LastName, u.Gender, "
			+ "u.Email,u.ContactNumber,u.Address,u.DateOfBirth,u.StatusId,c.Pan, c.Aadhar FROM Users u "
			+ "JOIN Customer c ON u.UserId = c.User_id JOIN Accounts a ON u.UserId = a.User_id WHERE a.account_number = ?";

	private static final String GET_CUSTOMER_DETAIL_BY_ID = "SELECT u.UserId, u.FirstName, u.LastName, u.Gender, "
			+ "u.Email,u.ContactNumber,u.Address,u.DateOfBirth,u.StatusId,u.TypeId From Users u WHERE u.UserId = ?";

	private static final String UPDATE_PASSWORD = "UPDATE Users SET Password = ?, UpdatedBy = ?, ModifiedBy = ? WHERE UserId = ?;";

	private static final String CHECK_EMPLOYEE_ID_EXISTS_QUERY = "SELECT COUNT(*) FROM Users u WHERE u.UserId = ? AND "
			+ "(u.TypeId = 2 OR u.TypeId = 3);";

	private static final String CHECK_CUSTOMER_EXISTS = "SELECT COUNT(*) FROM Customer WHERE Pan = ?;";

	private static final String GET_EMPLOYEE_DETAILS = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,u.ContactNumber,"
			+ " u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON u.UserId = e.user_id where "
			+ "u.UserId = ?";

	private static final String GET_ALL_EMPLOYEE_IN_ONE_BRANCH = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,"
			+ "u.ContactNumber,u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON "
			+ "u.UserId = e.user_id where e.branch_id = ? AND u.TypeId = 2";

	private static final String GET_ALL_EMPLOYEE_FROM_ALL_BRANCH = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,"
			+ "u.ContactNumber,u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON "
			+ "u.UserId = e.user_id WHERE u.TypeId = 2 ORDER BY e.branch_id;";
	
	private static final String UPDATE_CUSTOMER_DETAILS = "UPDATE Users SET FirstName = ?,LastName = ?,Gender = ?,Email = ?,"
			+ "ContactNumber = ?,Address = ?,DateOfBirth = ?,StatusId = ?,UpdatedBy = ?,ModifiedBy = ? WHERE UserId = ?;";

	private static final String GET_PASSWORD = "SELECT Password FROM Users WHERE UserId = ?";

	private static final String GET_TOKEN_STATUS = "SELECT statusId from TokenDB WHERE userId = ? AND token = ?;";

	@Override
	public User authendicateUser(int userID) throws CustomException {
		User user = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_USER)) {
			preparedStatement.setInt(1, userID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					user = new User();
					getUserDetails(resultSet, user);
				}
			}

		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception While Authendicating User Details",e);
			throw new CustomException("Exception While Authendicating User Details", e);
			
		}
		return user;
	}

	@Override
	public int addCustomer(Customer customer, int creatingUserId) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		int customerUserId = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createUserStatement = connection.prepareStatement(CREATE_NEW_USER,
						Statement.RETURN_GENERATED_KEYS)) {
			createUserStatement.setString(1, customer.getPassword());
			createUserStatement.setString(2, customer.getFirstName());
			createUserStatement.setString(3, customer.getLastName());
			createUserStatement.setString(4, customer.getGender());
			createUserStatement.setString(5, customer.getEmail());
			createUserStatement.setString(6, customer.getContactNumber());
			createUserStatement.setString(7, customer.getAddress());
			createUserStatement.setLong(8, customer.getDateOfBirth());
			createUserStatement.setInt(9, customer.getTypeOfUser().getValue());
			createUserStatement.setLong(10, System.currentTimeMillis());
			createUserStatement.setInt(11, creatingUserId);

			int rowsAffected = createUserStatement.executeUpdate();
			if (rowsAffected > 0) {
				int userId=0;
				try (ResultSet generatedKeys = createUserStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userId = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating user failed, no User ID obtained.");
					}
				}
				int rowsAffected1 = addCustomerPanAadhar(userId, customer);
				if (rowsAffected1 > 0) {
					customerUserId = userId;
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Creating new Customer",e);
			throw new CustomException("Exception Occured While Creating new Customer", e);
		}
		return customerUserId;
	}

	@Override
	public int addEmployee(Employee newEmployee, int creatingUserId) throws CustomException {
		InputValidator.isNull(newEmployee, ErrorMessages.INPUT_NULL_MESSAGE);
		int employeeId = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createUserStatement = connection.prepareStatement(CREATE_NEW_USER,
						Statement.RETURN_GENERATED_KEYS)) {
			createUserStatement.setString(1, newEmployee.getPassword());
			createUserStatement.setString(2, newEmployee.getFirstName());
			createUserStatement.setString(3, newEmployee.getLastName());
			createUserStatement.setString(4, newEmployee.getGender());
			createUserStatement.setString(5, newEmployee.getEmail());
			createUserStatement.setString(6, newEmployee.getContactNumber());
			createUserStatement.setString(7, newEmployee.getAddress());
			createUserStatement.setLong(8, newEmployee.getDateOfBirth());
			createUserStatement.setInt(9, newEmployee.getTypeOfUser().getValue());
			createUserStatement.setLong(10, System.currentTimeMillis());
			createUserStatement.setInt(11, creatingUserId);

			int rowsAffected = createUserStatement.executeUpdate();
			if (rowsAffected > 0) {
				int userId=0;
				try (ResultSet generatedKeys = createUserStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userId = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating user failed, no User ID obtained.");
					}
				}
				rowsAffected = addEmployeeToBranch(userId, newEmployee);
				if (rowsAffected > 0) {
					employeeId = userId;
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Creating new Employee",e);
			throw new CustomException("Exception Occured While Creating new Employee", e);
		}
		return employeeId;
	}

	@Override
	public int getEmployeeBranch(int userId) throws CustomException {
		int branchId = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_BRANCH)) {
			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					branchId = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Getting Employee Branch",e);
			throw new CustomException("Exception Occured While Getting Employee Branch", e);
		}
		return branchId;
	}
	
	@Override
	public int getTokenStatus(int userId,String userToken) throws CustomException {
		int status = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_TOKEN_STATUS)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, userToken);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					status = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Getting API Token",e);
			throw new CustomException("Exception Occured While Getting API Token", e);
		}
		return status;
	}

	@Override
	public boolean isValidCustomer(int userId) throws CustomException {
		boolean isValid = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CHECK_CUSTOMER_ID)) {
			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					isValid = (count > 0);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Validating Customer",e);
			throw new CustomException("Exception Occured While Validating Customer", e);
		}
		return isValid;
	}

	@Override
	public boolean isCustomerExists(String panNumber) throws CustomException {
		boolean userExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CHECK_CUSTOMER_EXISTS)) {
			preparedStatement.setString(1, panNumber);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					userExists = (count > 0);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Customer Exists",e);
			throw new CustomException("Exception Occured While Checking Customer Exists", e);
		}
		return userExists;
	}

	@Override
	public boolean checkCustomerIdPresentInBranch(int userID, int branchId) throws CustomException {
		boolean userIdExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement(CHECK_CUSTOMER_ID_EXISTS_QUERY_IN_BRANCH)) {
			preparedStatement.setInt(1, userID);
			preparedStatement.setInt(2, branchId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					userIdExists = (count > 0);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Customer Exists In Branch",e);
			throw new CustomException("Exception Occured While Checking Customer Exists In Branch", e);
		}
		return userIdExists;
	}

	@Override
	public Customer getCustomerDetails(String accountNumber) throws CustomException {
		Customer customerDetails = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement(GET_CUSTOMER_DETAIL_BY_ACCOUNT_NUMBER)) {
			preparedStatement.setString(1, accountNumber);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					customerDetails = getCustomerDetail(resultSet);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Customer Details",e);
			throw new CustomException("Exception Occured While Reterving Customer Details", e);
		}
		return customerDetails;
	}

	@Override
	public Customer getCustomerDetailsById(int userId) throws CustomException {
		Customer customerDetails = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_CUSTOMER_DETAIL_BY_ID)) {
			preparedStatement.setInt(1, userId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					customerDetails = getCustomerDetail(resultSet); 
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Customer Details",e);
			throw new CustomException("Exception Occured While Reterving Customer Details", e);
		}
		return customerDetails;
	}


	@Override
	public boolean updateCustomerDetails(Customer customer, int updatingUserId) throws CustomException {
		boolean isUpdated = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CUSTOMER_DETAILS)) {

			preparedStatement.setString(1, customer.getFirstName());
			preparedStatement.setString(2, customer.getLastName());
			preparedStatement.setString(3, customer.getGender());
			preparedStatement.setString(4, customer.getEmail());
			preparedStatement.setString(5, customer.getContactNumber());
			preparedStatement.setString(6, customer.getAddress());
			preparedStatement.setLong(7, customer.getDateOfBirth());
			preparedStatement.setInt(8, customer.getStatus().getValue());
			preparedStatement.setLong(9, System.currentTimeMillis());
			preparedStatement.setInt(10, updatingUserId);
			preparedStatement.setInt(11, customer.getUserId());

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				isUpdated = true;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Updating Customer Details",e);
			throw new CustomException("Exception Occured While Updating Customer Details", e);
		}
		return isUpdated;
	}

	@Override
	public boolean updatePassword(int userId, String password) throws CustomException {
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isPasswordUpdated = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PASSWORD)) {

			preparedStatement.setString(1, password);
			preparedStatement.setLong(2, System.currentTimeMillis());
			preparedStatement.setInt(3, userId);
			preparedStatement.setInt(4, userId);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				isPasswordUpdated = true;
			}

		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Updating Password",e);
			throw new CustomException("Exception Occured While Updating Password", e);
		}
		return isPasswordUpdated;
	}

	@Override
	public boolean checkEmployeeExists(int employeeId) throws CustomException {
		boolean employeeIdExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CHECK_EMPLOYEE_ID_EXISTS_QUERY)) {
			preparedStatement.setInt(1, employeeId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					employeeIdExists = (count > 0);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Employee Details",e);
			throw new CustomException("Exception Occured While Checking Employee Details", e);
		}
		return employeeIdExists;
	}

	@Override
	public Employee getEmployeeDetails(int employeeId) throws CustomException {
		Employee employeeDetails = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_DETAILS)) {
			preparedStatement.setInt(1, employeeId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					employeeDetails = new Employee();
					getEmployeeDetail(resultSet, employeeDetails);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return employeeDetails;
	}

	@Override
	public Map<Integer, Employee> getEmployeesInBranch(int branchId) throws CustomException {
		Map<Integer, Employee> employeeList = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_EMPLOYEE_IN_ONE_BRANCH)) {
			preparedStatement.setInt(1, branchId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				employeeList = new TreeMap<Integer, Employee>();
				getAllEmployeesDetail(resultSet, employeeList);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return employeeList;
	}

	@Override
	public Map<Integer, Map<Integer, Employee>> getEmployeesFromAllBranch() throws CustomException {
		Map<Integer, Map<Integer, Employee>> employeeList = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_EMPLOYEE_FROM_ALL_BRANCH)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				employeeList = new TreeMap<Integer, Map<Integer, Employee>>();
				getAllEmployeeDetailsByBranch(resultSet, employeeList);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving Employee Details",e);
			throw new CustomException("Exception Occured While Reterving Employee Details", e);
		}
		return employeeList;
	}

	@Override
	public String getUserPassword(int userId) throws CustomException {
		String password = null;
		try {
			try (Connection connection = DatabaseConnection.getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(GET_PASSWORD)) {

				preparedStatement.setInt(1, userId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						password = resultSet.getString(1);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving User Password",e);
			throw new CustomException("Exception Occured While Reterving User Password", e);
		}
		return password;
	}

	private int addCustomerPanAadhar(int userId, Customer customer) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createCustomerStatement = connection.prepareStatement(CREATE_CUSTOMER)) {
			createCustomerStatement.setInt(1, userId);
			createCustomerStatement.setString(2, customer.getPanNumber());
			createCustomerStatement.setString(3, customer.getAadharNumber());

			int rowsAfftected = createCustomerStatement.executeUpdate();
			return rowsAfftected;
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Reterving User Password",e);
			throw new CustomException("Error While Creating Customer ", e);
		}
	}

	private int addEmployeeToBranch(int userId, Employee employee) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createCustomerStatement = connection.prepareStatement(CREATE_EMPLOYEE)) {
			createCustomerStatement.setInt(1, userId);
			createCustomerStatement.setInt(2, employee.getBranchId());

			return createCustomerStatement.executeUpdate();
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Adding Employee into Employee Table",e);
			throw new CustomException("Exception Occured While Adding Employee into Employee Table", e);
		}
	}

	private void getAllEmployeesDetail(ResultSet resultSet, Map<Integer, Employee> employeesList) throws SQLException {
		Employee user;
		while (resultSet.next()) {
			user = new Employee();
			getEmployeeDetail(resultSet, user);
			employeesList.put(user.getUserId(), user);
		}
	}

	private void getEmployeeDetail(ResultSet resultSet, Employee employeeDetails) throws SQLException {
		getUserDetails(resultSet, employeeDetails);
		employeeDetails.setBranchId(resultSet.getInt(11));
	}

	private void getAllEmployeeDetailsByBranch(ResultSet resultSet, Map<Integer, Map<Integer, Employee>> employeeList)
			throws SQLException {
		Employee user;
		while (resultSet.next()) {
			user = new Employee();
			getEmployeeDetail(resultSet, user);
			int userId = user.getUserId();
			int branchId = user.getBranchId();
			Map<Integer, Employee> userMap = employeeList.computeIfAbsent(branchId, k -> new TreeMap<>());
			userMap.put(userId, user);
		}
	}

	private Customer getCustomerDetail(ResultSet resultSet) throws SQLException, CustomException {
		Customer customerDetails = new Customer();
		customerDetails.setUserId(resultSet.getInt(1));
		customerDetails.setFirstName(resultSet.getString(2));
		customerDetails.setLastName(resultSet.getString(3));
		customerDetails.setGender(resultSet.getString(4));
		customerDetails.setEmail(resultSet.getString(5));
		customerDetails.setContactNumber(resultSet.getString(6));
		customerDetails.setAddress(resultSet.getString(7));
		customerDetails.setDateOfBirth(resultSet.getLong(8));
		customerDetails.setStatus(resultSet.getInt(9));
		customerDetails.setTypeOfUser(resultSet.getInt(10));
		return customerDetails;
	}

	private void getUserDetails(ResultSet resultSet, User user) throws SQLException {
		user.setUserId(resultSet.getInt(1));
		user.setPassword(resultSet.getString(2));
		user.setFirstName(resultSet.getString(3));
		user.setLastName(resultSet.getString(4));
		user.setGender(resultSet.getString(5));
		user.setEmail(resultSet.getString(6));
		user.setContactNumber(resultSet.getString(7));
		user.setAddress(resultSet.getString(8));
		user.setDateOfBirth(resultSet.getLong(9));
		user.setTypeOfUser(resultSet.getInt(10));
		user.setStatus(resultSet.getInt(11));
	}
}
