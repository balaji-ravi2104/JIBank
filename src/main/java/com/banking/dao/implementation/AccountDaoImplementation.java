package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.banking.dao.AccountDao;
import com.banking.model.Account;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class AccountDaoImplementation implements AccountDao {

	private static final String CREATE_NEW_ACCOUNT = "INSERT INTO Accounts (user_id, account_number, "
			+ "branch_id,balance,primaryAccount,TypeId) VALUES (?,?,?,?,?,?);";

	private static final String GET_ACCOUNT_COUNT = "SELECT COUNT(*) FROM Accounts WHERE user_id = ?;";

	private static final String GET_COUNT_OF_ACCOUNT_IN_BRANCH = "SELECT COUNT(*) FROM Accounts WHERE branch_id = ?";

	private static final String GET_ACCOUNT_DETAILS = "SELECT * FROM Accounts WHERE account_number = ?;";

	private static final String GET_ACCOUNTS_IN_BRANCH = "select * FROM Accounts WHERE user_id = ? and branch_id = ?;";

	private static final String GET_ALL_ACCOUNTS_OF_CUSTOMER = "SELECT * FROM Accounts WHERE user_id = ?;";

	private static final String CHECK_CUSTOMER_ACCOUNT_EXISTS_IN_BRANCH = "SELECT COUNT(*) FROM Accounts "
			+ "WHERE account_number = ? and branch_id = ?;";

	private static final String UPDATE_BANK_ACCOUNT_STATUS = "UPDATE Accounts SET StatusId = ? WHERE account_number = ?;";

	@Override
	public boolean createAccount(Account account, boolean isPrimary) throws CustomException {
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

			int rowsAffected = preparedStatement.executeUpdate();
			isAccountCreated = rowsAffected > 0;

		} catch (SQLException e) {
			throw new CustomException("Error While Creating new Account!!!", e);
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

		} catch (SQLException e) {
			throw new CustomException("Error While Checking Account Existing!!!", e);
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
		} catch (SQLException e) {
			throw new CustomException("Error While Checking Account Existing!!!", e);
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
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving Account!!!", e);
		}
		return accountDetails;
	}

	@Override
	public Map<String, Account> getCustomerAccounts(int userId, int branchId) throws CustomException {
		Map<String, Account> customerAccounts = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCOUNTS_IN_BRANCH)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, branchId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				customerAccounts = new TreeMap<String, Account>();
				getAccounts(resultSet, customerAccounts);
			}

		} catch (SQLException e) {
			throw new CustomException("Error While Reterving Account!!!", e);
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

		} catch (SQLException e) {
			throw new CustomException("Error While Reterving All Account of a Customer!!!", e);
		}
		return allAccounts;
	}

	private void getAccountsByBranch(ResultSet resultSet, Map<Integer, Map<String, Account>> allAccounts)
			throws SQLException {
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

		} catch (SQLException e) {
			throw new CustomException("Error While Reterving All Account of a Customer!!!", e);
		}
		return accounts;
	}

	@Override
	public boolean activateDeactivateCustomerAccount(String accountNumber, int branchId, int status)
			throws CustomException {
		boolean isAccountStatusChanged = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BANK_ACCOUNT_STATUS)) {
			preparedStatement.setInt(1, status);
			preparedStatement.setString(2, accountNumber);
			int rowsAffected = preparedStatement.executeUpdate();
			isAccountStatusChanged = (rowsAffected > 0);
		} catch (SQLException e) {
			throw new CustomException("Error While Updating Bank Account Status", e);
		}
		return isAccountStatusChanged;
	}

	private void getAccounts(ResultSet resultSet, Map<String, Account> customerAccounts) throws SQLException {
		Account account;
		while (resultSet.next()) {
			account = new Account();
			getAccount(resultSet, account);
			customerAccounts.put(account.getAccountNumber(), account);
		}
	}

	private void getAllAccounts(ResultSet resultSet, List<Account> accounts) throws SQLException {
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
		} catch (SQLException e) {
			throw new CustomException("Error getting account count for branch ID: " + branchId, e);
		}
		return accountCount;
	}

	private void getAccount(ResultSet resultSet, Account account) throws SQLException {
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
