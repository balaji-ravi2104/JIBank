package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.AccountDao;
import com.banking.model.Account;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class AccountDaoImplementation implements AccountDao {

	private static final Logger logger = LoggerProvider.getLogger();

	private static final String CREATE_NEW_ACCOUNT = "INSERT INTO Accounts (user_id, account_number, "
			+ "branch_id,balance,primaryAccount,TypeId,CreatedBy,ModifiedBy) VALUES (?,?,?,?,?,?,?,?);";

	private static final String GET_ACCOUNT_COUNT = "SELECT COUNT(*) FROM Accounts WHERE user_id = ?;";

	private static final String GET_COUNT_OF_ACCOUNT_IN_BRANCH = "SELECT COUNT(*) FROM Accounts WHERE branch_id = ?";

	private static final String GET_ACCOUNT_DETAILS = "SELECT * FROM Accounts WHERE account_number = ?;";

	private static final String GET_ACCOUNTS_IN_BRANCH = "select * FROM Accounts WHERE user_id = ? and branch_id = ?;";

	private static final String GET_ALL_ACCOUNTS_OF_CUSTOMER = "SELECT * FROM Accounts WHERE user_id = ?;";

	private static final String CHECK_CUSTOMER_ACCOUNT_EXISTS_IN_BRANCH = "SELECT COUNT(*) FROM Accounts "
			+ "WHERE account_number = ? and branch_id = ?;";

	private static final String UPDATE_BANK_ACCOUNT_STATUS = "UPDATE Accounts SET StatusId = ?,UpdatedBy = ?,ModifiedBy = ? WHERE account_number = ?;";

	private static final String CUSTOMER_ALREADY_HAS_ACCOUNT_TYPE = "SELECT COUNT(*) TypeId FROM Accounts WHERE user_id = ? and branch_id = ? and Typeid = ?;";

	private static final String IS_ACCOUNT_PRESENT = "SELECT COUNT(*) AS account_count FROM Accounts WHERE account_number = ?;";

	@Override
	public boolean createAccount(Account account, boolean isPrimary, int creatingUserId) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAccountCreated = false;
		String accountNumber = String.format("%04d%08d", account.getBranchId(),
				getAccountCountInBranch(account.getBranchId()) + 1);
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CREATE_NEW_ACCOUNT)) {
			preparedStatement.setInt(1, account.getUserId());
			preparedStatement.setString(2, accountNumber);
			preparedStatement.setInt(3, account.getBranchId());
			preparedStatement.setDouble(4, account.getBalance());
			preparedStatement.setBoolean(5, isPrimary);
			preparedStatement.setInt(6, account.getAccountType().getValue());
			preparedStatement.setLong(7, System.currentTimeMillis());
			preparedStatement.setInt(8, creatingUserId);

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				isAccountCreated = true;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Creating new Account", e);
			throw new CustomException("Exception Occured While Creating new Account", e);
		}
		return isAccountCreated;
	}

	@Override
	public boolean checkAccountExists(String accountNumber, int branchId) throws CustomException {
		boolean isAccountExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement(CHECK_CUSTOMER_ACCOUNT_EXISTS_IN_BRANCH)) {
			preparedStatement.setString(1, accountNumber);
			preparedStatement.setInt(2, branchId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					isAccountExists = (count > 0);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Checking Account Exists in Branch", e);
			throw new CustomException("Exception Occured While Checking Account Exists in Branch", e);
		}
		return isAccountExists;
	}

	@Override
	public boolean customerHasAccount(int userId) throws CustomException {
		boolean isAccountExists = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCOUNT_COUNT)) {
			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					isAccountExists = (resultSet.getInt(1) > 0);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Checking Account Exists", e);
			throw new CustomException("Exception Occured While Checking Account Exists", e);
		}
		return isAccountExists;
	}

	@Override
	public Account getAccountDetail(String accountNumber) throws CustomException {
		Account accountDetails = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCOUNT_DETAILS)) {

			preparedStatement.setString(1, accountNumber);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					accountDetails = new Account();
					getAccount(resultSet, accountDetails);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Account Details", e);
			throw new CustomException("Exception Occured While Reterving Account Details", e);
		}
		return accountDetails;
	}

	@Override
	public Map<String, Account> getCustomerAccounts(int userId, int branchId) throws CustomException {
		// System.out.println(userId + " " + branchId);
		Map<String, Account> customerAccounts = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCOUNTS_IN_BRANCH)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, branchId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				customerAccounts = new TreeMap<String, Account>();
				getAccounts(resultSet, customerAccounts);
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Account Details", e);
			throw new CustomException("Exception Occured While Reterving Account Details", e);
		}
		return customerAccounts;
	}

	@Override
	public Map<Integer, Map<String, Account>> getCustomersAllAccount(int userId) throws CustomException {
		Map<Integer, Map<String, Account>> allAccounts = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_ACCOUNTS_OF_CUSTOMER)) {

			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				allAccounts = new TreeMap<Integer, Map<String, Account>>();
				getAccountsByBranch(resultSet, allAccounts);
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving All Account Details", e);
			throw new CustomException("Exception Occured While Reterving All Account Details", e);
		}
		return allAccounts;
	}

	@Override
	public boolean isCustomerAlreadyHasAccount(int userId, int accountType, int branchId) throws CustomException {
		boolean isAccountPresent = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CUSTOMER_ALREADY_HAS_ACCOUNT_TYPE)) {

			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, branchId);
			preparedStatement.setInt(3, accountType);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					isAccountPresent = (count > 0);
				}
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Account Type Details", e);
			throw new CustomException("Exception Occured While Reterving Account Type Details", e);
		}
		return isAccountPresent;
	}

	@Override
	public boolean isAccountPresent(String accountNumber) throws CustomException {
		boolean isAccountPresent = false;
		// System.out.println(accountNumber);
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(IS_ACCOUNT_PRESENT)) {

			preparedStatement.setString(1, accountNumber);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					isAccountPresent = (count > 0);
				}
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured while Checking Account Exists", e);
			throw new CustomException("Exception Occured while Checking Account Exists", e);
		}
		return isAccountPresent;
	}

	private void getAccountsByBranch(ResultSet resultSet, Map<Integer, Map<String, Account>> allAccounts)
			throws Exception {
		Account account;
		while (resultSet.next()) {
			account = new Account();
			getAccount(resultSet, account);
			int branchId = account.getBranchId();
			String accountNumber = account.getAccountNumber();

			allAccounts.computeIfAbsent(branchId, k -> new TreeMap<String, Account>());
			Map<String, Account> branchAccounts = allAccounts.get(branchId);
			branchAccounts.put(accountNumber, account);
		}
	}

	@Override
	public List<Account> getAllAccountsOfCustomer(int userId) throws CustomException {
		List<Account> accounts = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_ACCOUNTS_OF_CUSTOMER)) {

			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				accounts = new ArrayList<Account>();
				getAllAccounts(resultSet, accounts);
			}

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving All Account of a Customer", e);
			throw new CustomException("Exception Occured While Reterving All Account of a Customer", e);
		}
		return accounts;
	}

	@Override
	public boolean changeAccountStatus(String accountNumber, int status, int updatingUserId)
			throws CustomException {
		boolean isAccountStatusChanged = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BANK_ACCOUNT_STATUS)) {
			preparedStatement.setInt(1, status);
			preparedStatement.setLong(2, System.currentTimeMillis());
			preparedStatement.setInt(3, updatingUserId);
			preparedStatement.setString(4, accountNumber);
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				isAccountStatusChanged = true;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Updating Bank Account Status", e);
			throw new CustomException("Exception Occured While Updating Bank Account Status", e);
		}
		return isAccountStatusChanged;
	}

	private void getAccounts(ResultSet resultSet, Map<String, Account> customerAccounts) throws Exception {
		Account account;
		while (resultSet.next()) {
			account = new Account();
			getAccount(resultSet, account);
			customerAccounts.put(account.getAccountNumber(), account);
		}
	}

	private void getAllAccounts(ResultSet resultSet, List<Account> accounts) throws Exception {
		Account account;
		while (resultSet.next()) {
			account = new Account();
			getAccount(resultSet, account);
			accounts.add(account);
		}
	}

	private int getAccountCountInBranch(int branchId) throws CustomException {
		int accountCount = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_COUNT_OF_ACCOUNT_IN_BRANCH)) {
			preparedStatement.setInt(1, branchId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					accountCount = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured Whilegetting account count of branch", e);
			throw new CustomException("Exception Occured While getting account count of branch", e);
		}
		return accountCount;
	}

	private void getAccount(ResultSet resultSet, Account account) throws Exception {
		account.setAccountId(resultSet.getInt(1));
		account.setUserId(resultSet.getInt(2));
		account.setAccountNumber(resultSet.getString(3));
		account.setBranchId(resultSet.getInt(4));
		account.setBalance(resultSet.getDouble(5));
		account.setPrimaryAccount(resultSet.getBoolean(6));
		account.setAccountType(resultSet.getInt(7));
		account.setAccountStatus(resultSet.getInt(8));
	}
}
