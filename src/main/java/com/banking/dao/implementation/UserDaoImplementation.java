package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.banking.dao.UserDao;
import com.banking.model.Customer;
import com.banking.model.Employee;
import com.banking.model.User;
import com.banking.utils.CommonUtils.Field;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class UserDaoImplementation implements UserDao {

	private static final String GET_USER = "SELECT  u.userId,u.FirstName,u.LastName,u.Gender,u.Email,"
			+ "u.ContactNumber,u.Address,u.DateOfBirth,u.TypeId,u.StatusId FROM Users u WHERE u.userId = ? and u.password = ?";

	private static final String GET_EMPLOYEE_BRANCH = "SELECT branch_id FROM Employee WHERE User_id = ?";

	private static final String CREATE_EMPLOYEE = "INSERT INTO Employee (User_id,branch_id) Values (?,?);";

	private static final String CREATE_NEW_USER = "INSERT INTO Users (Password, FirstName, LastName, Gender, Email, "
			+ "ContactNumber, Address, DateOfBirth, TypeId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String CREATE_CUSTOMER = "INSERT INTO Customer (User_id, Pan, Aadhar) VALUES (?, ?, ?);";

	private static final String CHECK_USER_ID_EXISTS = "SELECT COUNT(*) FROM Users u WHERE u.UserId = ? "
			+ "AND u.TypeId = 1;";

	private static final String CHECK_CUSTOMER_ID_EXISTS_QUERY_IN_BRANCH = "SELECT COUNT(*) FROM Users u JOIN "
			+ "Accounts a ON u.UserId = a.user_id WHERE u.UserId = ? AND a.branch_id = ? AND u.TypeId = 1;";

	private static final String GET_CUSTOMER_DETAIL_BY_ACCOUNT_NUMBER = "SELECT u.UserId, u.FirstName, u.LastName, u.Gender, "
			+ "u.Email,u.ContactNumber,u.Address,u.DateOfBirth,u.StatusId,c.Pan, c.Aadhar FROM Users u "
			+ "JOIN Customer c ON u.UserId = c.User_id JOIN Accounts a ON u.UserId = a.User_id WHERE a.account_number = ?";

	private static final String GET_CUSTOMER_DETAIL_BY_ID = "SELECT u.UserId, u.FirstName, u.LastName, u.Gender, "
			+ "u.Email,u.ContactNumber,u.Address,u.DateOfBirth,u.StatusId,c.Pan, c.Aadhar FROM Users u "
			+ "JOIN Customer c ON u.UserId = c.User_id WHERE u.UserId = ?";

	private static final String UPDATE_PASSWORD = "UPDATE Users SET Password = ? WHERE UserId = ?;";

	private static final String CHECK_EMPLOYEE_ID_EXISTS_QUERY = "SELECT COUNT(*) FROM Users u WHERE u.UserId = ? AND "
			+ "u.TypeId = 2;";

	private static final String GET_EMPLOYEE_DETAILS = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,u.ContactNumber,"
			+ " u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON u.UserId = e.user_id where "
			+ "u.UserId = ?";

	private static final String GET_ALL_EMPLOYEE_IN_ONE_BRANCH = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,"
			+ "u.ContactNumber,u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON "
			+ "u.UserId = e.user_id where e.branch_id = ? AND u.TypeId = 2";

	private static final String GET_ALL_EMPLOYEE_FROM_ALL_BRANCH = "SELECT u.UserId,u.FirstName,u.LastName,u.Gender,u.Email,"
			+ "u.ContactNumber,u.Address,u.DateOfBirth,u.TypeId,u.StatusId,e.branch_id FROM Users u INNER JOIN Employee e ON "
			+ "u.UserId = e.user_id WHERE u.TypeId = 2 ORDER BY e.branch_id;";

	@Override
	public User authendicateUser(int userID, String password) throws CustomException {
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		User user = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_USER)) {
			preparedStatement.setInt(1, userID);
			preparedStatement.setString(2, password);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					user = new User();
					getUserDetails(resultSet, user);
				}
			}

		} catch (SQLException e) {
			throw new CustomException("Error While Reterving User Details", e);
		}
		return user;
	}

	@Override
	public boolean addCustomer(Customer customer) throws CustomException {
		InputValidator.isNull(customer, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isCustomerCreated = false;
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

			int rowsAffected = createUserStatement.executeUpdate();
			if (rowsAffected > 0) {
				int userId;
				try (ResultSet generatedKeys = createUserStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userId = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating user failed, no User ID obtained.");
					}
				}
				int rowsAffected1 = addCustomerPanAadhar(userId, customer);
				if (rowsAffected1 > 0) {
					isCustomerCreated = true;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error Creating new User", e);
		}
		return isCustomerCreated;
	}

	@Override
	public boolean addEmployee(Employee newEmployee) throws CustomException {
		InputValidator.isNull(newEmployee, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isCustomerCreated = false;
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

			int rowsAffected = createUserStatement.executeUpdate();
			if (rowsAffected > 0) {
				int userId;
				try (ResultSet generatedKeys = createUserStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userId = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating user failed, no User ID obtained.");
					}
				}
				rowsAffected = addEmployeeToBranch(userId, newEmployee);
				if (rowsAffected > 0) {
					isCustomerCreated = true;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error Creating new User", e);
		}
		return isCustomerCreated;
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
			throw new CustomException("Error Creating new User", e);
		}
		return branchId;
	}

	@Override
	public boolean checkUserIdExists(int userId) throws CustomException {
		boolean userIdExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_ID_EXISTS)) {
			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					userIdExists = (count > 0);
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Checking User Details", e);
		}
		return userIdExists;
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
			throw new CustomException("Error While Checking User Details", e);
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
			throw new CustomException("Error While Reterving User Details", e);
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
			throw new CustomException("Error While Reterving User Details", e);
		}
		return customerDetails;
	}

	@Override
	public <K extends Enum<K>, V> boolean updateCustomerDetails(int userIdToUpdate, Map<K, V> fieldsToUpdate)
			throws CustomException {
		String updateQuery = generateUpdateQuery(fieldsToUpdate);
		boolean isUpdated = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
			int index = 1;
			for (Map.Entry<K, V> entry : fieldsToUpdate.entrySet()) {
				if (entry.getKey().equals(Field.Pan) || entry.getKey().equals(Field.Aadhar)) {
					continue;
				}
				preparedStatement.setObject(index++, entry.getValue());
			}
			if (fieldsToUpdate.containsKey(Field.Pan)) {
				preparedStatement.setObject(index++, fieldsToUpdate.get(Field.Pan));
			}
			if (fieldsToUpdate.containsKey(Field.Aadhar)) {
				preparedStatement.setObject(index++, fieldsToUpdate.get(Field.Aadhar));
			}
			preparedStatement.setInt(index++, userIdToUpdate);
			int rowsAffected = preparedStatement.executeUpdate();
			isUpdated = (rowsAffected > 0);
		} catch (SQLException e) {
			throw new CustomException("Error While Updating User Details", e);
		}
		return isUpdated;
	}

	@Override
	public boolean updatePassword(int userId, String password) throws CustomException {
		InputValidator.isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PASSWORD)) {

			preparedStatement.setString(1, password);
			preparedStatement.setInt(2, userId);

			return preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new CustomException("Error While Updating User Details", e);
		}
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
		} catch (SQLException e) {
			throw new CustomException("Error While Checking User Details", e);
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
			throw new CustomException("Error While Reterving User Details", e);
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
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving User Details", e);
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
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving User Details", e);
		}
		return employeeList;
	}

	private int addCustomerPanAadhar(int userId, Customer customer) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createCustomerStatement = connection.prepareStatement(CREATE_CUSTOMER)) {
			createCustomerStatement.setInt(1, userId);
			createCustomerStatement.setString(2, customer.getPanNumber());
			createCustomerStatement.setString(3, customer.getAadharNumber());

			int rowsAfftected = createCustomerStatement.executeUpdate();
			return rowsAfftected;
		} catch (SQLException e) {
			throw new CustomException("Error While Creating Customer ", e);
		}
	}

	private int addEmployeeToBranch(int userId, Employee employee) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement createCustomerStatement = connection.prepareStatement(CREATE_EMPLOYEE)) {
			createCustomerStatement.setInt(1, userId);
			createCustomerStatement.setInt(2, employee.getBranchId());

			return createCustomerStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CustomException("Error While Creating Employee ", e);
		}
	}

	private <K extends Enum<K>, V> String generateUpdateQuery(Map<K, V> fieldsToUpdate) {
		StringBuilder queryBuilder = new StringBuilder("UPDATE Users u JOIN Customer c ON u.UserId = c.User_id SET ");
		Iterator<Map.Entry<K, V>> iterator = fieldsToUpdate.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<K, V> entry = iterator.next();
			K key = entry.getKey();
			if (key.equals(Field.Pan) || key.equals(Field.Aadhar)) {
				continue;
			}
			queryBuilder.append("u.").append(key.name()).append(" = ?");
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		if (fieldsToUpdate.containsKey(Field.Pan) || fieldsToUpdate.containsKey(Field.Aadhar)) {
			queryBuilder.append("c.");
			if (fieldsToUpdate.containsKey(Field.Pan)) {
				queryBuilder.append(Field.Pan + " = ?");
			}
			if (fieldsToUpdate.containsKey(Field.Aadhar)) {
				if (fieldsToUpdate.containsKey(Field.Pan)) {
					queryBuilder.append(", ");
				}
				queryBuilder.append(Field.Aadhar + " = ?");
			}
		}
		queryBuilder.append(" WHERE u.UserId = ?");
		return queryBuilder.toString();
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
		customerDetails.setPanNumber(resultSet.getString(10));
		customerDetails.setAadharNumber(resultSet.getString(11));
		return customerDetails;
	}

	private void getUserDetails(ResultSet resultSet, User user) throws SQLException {
		user.setUserId(resultSet.getInt(1));
		user.setFirstName(resultSet.getString(2));
		user.setLastName(resultSet.getString(3));
		user.setGender(resultSet.getString(4));
		user.setEmail(resultSet.getString(5));
		user.setContactNumber(resultSet.getString(6));
		user.setAddress(resultSet.getString(7));
		user.setDateOfBirth(resultSet.getLong(8));
		user.setTypeOfUser(resultSet.getInt(9));
		user.setStatus(resultSet.getInt(10));
	}

//	private void getCustomerPanAndAadhar(int userID, User user) throws CustomException {
//	try (Connection connection = DatabaseConnection.getConnection();
//			PreparedStatement preparedStatement = connection.prepareStatement(GET_CUSTOMER_QUERY)) {
//		preparedStatement.setInt(1, userID);
//		try (ResultSet resultSet = preparedStatement.executeQuery()) {
//			if (resultSet.next()) {
//				user.setPanNumber(resultSet.getString(1));
//				user.setAadharNumber(resultSet.getString(2));
//			}
//		}
//	} catch (SQLException e) {
//		throw new CustomException("Error While Reterving Customer Details", e);
//	}
//}
}
