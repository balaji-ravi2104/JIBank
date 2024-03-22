package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.banking.dao.TransactionDao;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.TransactionStatus;
import com.banking.model.TransactionType;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;

public class TransactionDaoImplementation implements TransactionDao {

	private static final String UPDATE_QUERY = "UPDATE Accounts SET balance = ? WHERE account_number = ?;";

	private static final String TRANSACTION_LOG = "INSERT INTO Transaction (user_id, viewer_account_number, "
			+ "transacted_account_number, TypeId, transaction_amount, balance, transaction_date, "
			+ "remark, StatusId,reference_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?);";

	private static final String GET_STATEMENT = "SELECT transaction_date, TypeId,transaction_amount, "
			+ "balance FROM Transaction WHERE viewer_account_number = ? AND FROM_UNIXTIME(transaction_date / 1000) >= "
			+ "DATE_SUB(CURRENT_DATE(), INTERVAL ? MONTH) order by transaction_id DESC";

	private static final String GET_TRANSACTION_HISTORY = "select * From Transaction WHERE viewer_account_number = ? AND "
			+ "FROM_UNIXTIME(transaction_date / 1000) >= DATE_SUB(CURRENT_DATE(), INTERVAL ? MONTH) ORDER BY transaction_id DESC;";

	private static final String GET_ALL_TRANSACTION_OF_CUSTOMER_IN_BRANCH = "SELECT t.transaction_id, t.user_id, "
			+ "t.viewer_account_number, t.transacted_account_number, t.TypeId, t.transaction_amount, "
			+ "t.balance, t.transaction_date, t.remark, t.statusId,t.reference_id FROM Transaction t JOIN Accounts a ON "
			+ "t.viewer_account_number = a.account_number WHERE a.user_id = ? AND a.branch_id = ? AND "
			+ "FROM_UNIXTIME(t.transaction_date / 1000) >= DATE_SUB(CURRENT_DATE(), INTERVAL ? MONTH) ORDER BY t.transaction_id DESC;";

	@Override
	public boolean deposit(Account account, double amountToDeposit) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAmountDepositedAndLoggedInTransaction = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			if (amountToDeposit <= 0) {
				throw new CustomException("Amount to be Deposited Should be Greater than ZERO!!!");
			}
			boolean isBalanceUpdated = updateAccountBalance(connection, account,
					account.getBalance() + amountToDeposit);
			if (isBalanceUpdated) {
				isAmountDepositedAndLoggedInTransaction = logTransaction(account, account.getAccountNumber(),
						amountToDeposit, TransactionType.DEPOSIT.name(), TransactionType.DEPOSIT.getValue(),
						account.getUserId() + System.currentTimeMillis());
				if (isAmountDepositedAndLoggedInTransaction) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Depositing Money", e);
		}
		return isAmountDepositedAndLoggedInTransaction;
	}

	@Override
	public boolean withdraw(Account account, double amountToWithdraw) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAmountWithdrawnAndLoggedInTransaction = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			if (account.getBalance() < amountToWithdraw) {
				throw new CustomException("Insufficient Balance");
			}
			boolean isBalanceUpdated = updateAccountBalance(connection, account,
					account.getBalance() - amountToWithdraw);
			if (isBalanceUpdated) {
				isAmountWithdrawnAndLoggedInTransaction = logTransaction(account, account.getAccountNumber(),
						amountToWithdraw, TransactionType.WITHDRAW.name(), TransactionType.WITHDRAW.getValue(),
						account.getUserId() + System.currentTimeMillis());
				if (isAmountWithdrawnAndLoggedInTransaction) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Withdrawing Money", e);
		}
		return isAmountWithdrawnAndLoggedInTransaction;
	}

	@Override
	public boolean transferMoneyWithinBank(Account accountFromTransfer, Account accountToTransfer,
			double amountToTransfer, String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		if (accountFromTransfer.getBalance() < amountToTransfer) {
			throw new CustomException("Insufficient Balance");
		}
		boolean isTransferSuccess = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);

			double newBalanceOfFromAccount = accountFromTransfer.getBalance() - amountToTransfer;
			boolean isFromAccountBalanceUpdated = updateAccountBalance(connection, accountFromTransfer,
					newBalanceOfFromAccount);

			if (isFromAccountBalanceUpdated) {
				double newBalanceOfToAccount = accountToTransfer.getBalance() + amountToTransfer;
				boolean isToAccountBalanceUpdated = updateAccountBalance(connection, accountToTransfer,
						newBalanceOfToAccount);

				if (isToAccountBalanceUpdated) {
					long referenceId = accountFromTransfer.getUserId() + System.currentTimeMillis();
					boolean isTransactionLoggedWithdraw = logTransaction(accountFromTransfer,
							accountToTransfer.getAccountNumber(), amountToTransfer, remark,
							TransactionType.WITHDRAW.getValue(), referenceId);
					boolean isTransactionLoggedDeposit = logTransaction(accountToTransfer,
							accountFromTransfer.getAccountNumber(), amountToTransfer, remark,
							TransactionType.DEPOSIT.getValue(), referenceId);

					if (isTransactionLoggedWithdraw && isTransactionLoggedDeposit) {
						connection.commit();
						isTransferSuccess = true;
					} else {
						connection.rollback();
					}
				} else {
					connection.rollback();
				}
			} else {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Transferring Money", e);
		}
		return isTransferSuccess;
	}

	@Override
	public boolean transferMoneyWithOtherBank(Account accountFromTransfer, String accountNumberToTransfer,
			double amountToTransferWithOtherBank, String remark) throws CustomException {
		InputValidator.isNull(accountFromTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(accountNumberToTransfer, ErrorMessages.INPUT_NULL_MESSAGE);
		InputValidator.isNull(remark, ErrorMessages.INPUT_NULL_MESSAGE);
		if (accountFromTransfer.getBalance() < amountToTransferWithOtherBank) {
			throw new CustomException("Insufficient Balance");
		}
		boolean isTransferSuccess = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			double newBalanceOfFromAccount = accountFromTransfer.getBalance() - amountToTransferWithOtherBank;

			boolean isFromAccountBalanceUpdated = updateAccountBalance(connection, accountFromTransfer,
					newBalanceOfFromAccount);

			if (isFromAccountBalanceUpdated) {
				isTransferSuccess = logTransaction(accountFromTransfer, accountNumberToTransfer,
						amountToTransferWithOtherBank, remark, TransactionType.WITHDRAW.getValue(),
						accountFromTransfer.getUserId() + System.currentTimeMillis());
				if (isTransferSuccess) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (SQLException e) {
		}
		return isTransferSuccess;
	}

	@Override
	public List<Transaction> getUsersStatement(Account account, int numberOfMonths) throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		List<Transaction> statements = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_STATEMENT)) {

			preparedStatement.setString(1, account.getAccountNumber());
			preparedStatement.setInt(2, numberOfMonths);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				statements = new ArrayList<Transaction>();
				getStatementDetails(resultSet, statements);
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving Transaction!!!", e);
		}
		return statements;
	}

	@Override
	public List<Transaction> getCustomerTransactionHistory(String accountNumber, int month) throws CustomException {
		List<Transaction> historyList = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_TRANSACTION_HISTORY)) {

			preparedStatement.setString(1, accountNumber);
			preparedStatement.setInt(2, month);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				historyList = new ArrayList<Transaction>();
				getCustomerTransactionDetail(resultSet, historyList);
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving Transaction!!!", e);
		}
		return historyList;
	}

	@Override
	public Map<String, List<Transaction>> getAllTransactionHistory(int userId, int branchId, int month)
			throws CustomException {
		Map<String, List<Transaction>> transactionMap = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement(GET_ALL_TRANSACTION_OF_CUSTOMER_IN_BRANCH)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, branchId);
			preparedStatement.setInt(3, month);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				transactionMap = new HashMap<String, List<Transaction>>();
				getCustomersTransactionDetail(resultSet, transactionMap);
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Reterving Transaction!!!", e);
		}
		return transactionMap;
	}

	private void getCustomerTransactionDetail(ResultSet resultSet, List<Transaction> historyList) throws SQLException {
		while (resultSet.next()) {
			historyList.add(getTransactionDetail(resultSet));
		}
	}

	private void getCustomersTransactionDetail(ResultSet resultSet, Map<String, List<Transaction>> transactionList)
			throws SQLException {
		while (resultSet.next()) {
			Transaction transaction = getTransactionDetail(resultSet);
			String accountNumber = transaction.getViewerAccount();
			transactionList.computeIfAbsent(accountNumber, k -> new ArrayList<Transaction>()).add(transaction);
		}
	}

	private Transaction getTransactionDetail(ResultSet resultSet) throws SQLException {
		Transaction transaction = new Transaction();
		transaction.setTransactionId(resultSet.getInt(1));
		transaction.setUserId(resultSet.getInt(2));
		transaction.setViewerAccount(resultSet.getString(3));
		transaction.setTransactedAccount(resultSet.getString(4));
		transaction.setTransactionType(resultSet.getInt(5));
		transaction.setTransactedAmount(resultSet.getDouble(6));
		transaction.setBalance(resultSet.getDouble(7));
		transaction.setDateOfTransaction(resultSet.getLong(8));
		transaction.setRemark(resultSet.getString(9));
		transaction.setStatus(resultSet.getInt(10));
		transaction.setReferenceId(resultSet.getLong(11));
		return transaction;
	}

	private void getStatementDetails(ResultSet resultSet, List<Transaction> statements) throws SQLException {
		Transaction transaction;
		while (resultSet.next()) {
			transaction = new Transaction();
			transaction.setDateOfTransaction(resultSet.getLong(1));
			transaction.setTransactionType(resultSet.getInt(2));
			transaction.setTransactedAmount(resultSet.getDouble(3));
			transaction.setBalance(resultSet.getDouble(4));

			statements.add(transaction);
		}
	}

	private boolean logTransaction(Account viewerAccount, String transactedAccountNumber, double amount, String remark,
			int transactionType, long referenceId) throws CustomException {
		boolean isLoggedSuccessfully = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(TRANSACTION_LOG)) {
			preparedStatement.setInt(1, viewerAccount.getUserId());
			preparedStatement.setString(2, viewerAccount.getAccountNumber());
			preparedStatement.setString(3, transactedAccountNumber);
			preparedStatement.setInt(4, transactionType);
			preparedStatement.setDouble(5, amount);
			preparedStatement.setDouble(6, viewerAccount.getBalance());
			preparedStatement.setLong(7, System.currentTimeMillis());
			preparedStatement.setString(8, remark);
			preparedStatement.setInt(9, TransactionStatus.SUCCESS.getValue());
			preparedStatement.setLong(10, referenceId);

			int rowsAffected = preparedStatement.executeUpdate();

			isLoggedSuccessfully = (rowsAffected > 0);

		} catch (SQLException e) {
			throw new CustomException("Error While Logging Details fIn Transaction", e);
		}
		return isLoggedSuccessfully;
	}

	private boolean updateAccountBalance(Connection connection, Account account, double amountToUpdate)
			throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isBalanceUpdated = false;
		try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

			preparedStatement.setDouble(1, amountToUpdate);
			preparedStatement.setString(2, account.getAccountNumber());

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				account.setBalance(amountToUpdate);
				isBalanceUpdated = true;
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Updating Balance!!!", e);
		}
		return isBalanceUpdated;
	}

}
