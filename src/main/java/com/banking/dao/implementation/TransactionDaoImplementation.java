package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.TransactionDao;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.TransactionStatus;
import com.banking.model.TransactionType;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.ErrorMessages;
import com.banking.utils.InputValidator;
import com.banking.utils.LoggerProvider;

public class TransactionDaoImplementation implements TransactionDao {

	private static final Logger logger = LoggerProvider.getLogger();

	private static final String UPDATE_QUERY = "UPDATE Accounts SET balance = ? WHERE account_number = ?;";

	private static final String TRANSACTION_LOG = "INSERT INTO Transaction (user_id, viewer_account_number, "
			+ "transacted_account_number, TypeId, transaction_amount, balance, transaction_date, "
			+ "remark, StatusId,reference_id,CreatedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);";

	private static final String GET_TRANSACTION_HISTORY = "select * From Transaction WHERE viewer_account_number = ? AND "
			+ "FROM_UNIXTIME(transaction_date / 1000) >= DATE_SUB(CURRENT_DATE(), INTERVAL ? MONTH) ORDER BY transaction_id DESC;";

	private static final String GET_ALL_TRANSACTION_OF_CUSTOMER_IN_BRANCH = "SELECT t.transaction_id, t.user_id, "
			+ "t.viewer_account_number, t.transacted_account_number, t.TypeId, t.transaction_amount, "
			+ "t.balance, t.transaction_date, t.remark, t.statusId,t.reference_id FROM Transaction t JOIN Accounts a ON "
			+ "t.viewer_account_number = a.account_number WHERE a.user_id = ? AND a.branch_id = ? AND "
			+ "FROM_UNIXTIME(t.transaction_date / 1000) >= DATE_SUB(CURRENT_DATE(), INTERVAL ? MONTH) ORDER BY t.transaction_id DESC;";

	private static final String GET_TRANSACTIONS = "SELECT  t.transaction_id, t.user_id,t.viewer_account_number, "
			+ "t.transacted_account_number, t.TypeId, t.transaction_amount,t.balance, t.transaction_date, t.remark, "
			+ "t.statusId,t.reference_id FROM Transaction t WHERE t.viewer_account_number = ? AND "
			+ "FROM_UNIXTIME(transaction_date / 1000) BETWEEN ? AND ? ORDER BY t.transaction_id DESC;";

	private static final String GET_CURRENT_BALANCE = "SELECT balance FROM Accounts WHERE account_number = ?;";

	@Override
	public boolean deposit(Account account, double amountToDeposit, String description, int userId)
			throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAmountDepositedAndLoggedInTransaction = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			if (amountToDeposit <= 0) {
				throw new CustomException("Amount to be Deposited Should be Greater than ZERO!!!");
			}
			double oldBalance = getCurrentBalance(account.getAccountNumber());
			double newBalance = oldBalance + amountToDeposit;
			boolean isBalanceUpdated = updateAccountBalance(connection, account, newBalance);
			if (isBalanceUpdated) {
				isAmountDepositedAndLoggedInTransaction = logTransaction(account, account.getAccountNumber(),
						amountToDeposit, description, TransactionType.DEPOSIT.getValue(),
						account.getUserId() + System.currentTimeMillis());
				if (isAmountDepositedAndLoggedInTransaction) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Depositing Money", e);
			throw new CustomException("Exception Occured While Depositing Money", e);
		}
		return isAmountDepositedAndLoggedInTransaction;
	}

	private double getCurrentBalance(String accountNumber) throws CustomException {
		InputValidator.isNull(accountNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		double balance = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_CURRENT_BALANCE)) {

			preparedStatement.setString(1, accountNumber);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					balance = resultSet.getDouble(1);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Getting Balance", e);
			throw new CustomException("Exception Occured While Getting Balance", e);
		}
		return balance;
	}

	@Override
	public boolean withdraw(Account account, double amountToWithdraw, String description, int userId)
			throws CustomException {
		InputValidator.isNull(account, ErrorMessages.INPUT_NULL_MESSAGE);
		boolean isAmountWithdrawnAndLoggedInTransaction = false;
		try (Connection connection = DatabaseConnection.getConnection()) {
			connection.setAutoCommit(false);
			double oldBalance = getCurrentBalance(account.getAccountNumber());
			if (oldBalance < amountToWithdraw) {
				throw new CustomException("Insufficient Balance");
			}
			double newBalance = oldBalance - amountToWithdraw;
			boolean isBalanceUpdated = updateAccountBalance(connection, account, newBalance);
			if (isBalanceUpdated) {
				isAmountWithdrawnAndLoggedInTransaction = logTransaction(account, account.getAccountNumber(),
						amountToWithdraw, description, TransactionType.WITHDRAW.getValue(),
						account.getUserId() + System.currentTimeMillis());
				if (isAmountWithdrawnAndLoggedInTransaction) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Withdrawing Money", e);
			throw new CustomException("Exception Occured While Withdrawing Money", e);
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

			double oldBalanceOfFromAccount = getCurrentBalance(accountFromTransfer.getAccountNumber());
			double newBalanceOfFromAccount = oldBalanceOfFromAccount - amountToTransfer;
			boolean isFromAccountBalanceUpdated = updateAccountBalance(connection, accountFromTransfer,
					newBalanceOfFromAccount);

			if (isFromAccountBalanceUpdated) {
				double oldBalanceOfAccountToTransfer = getCurrentBalance(accountToTransfer.getAccountNumber());
				double newBalanceOfToAccount = oldBalanceOfAccountToTransfer + amountToTransfer;
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Transferring Money Within Bank", e);
			throw new CustomException("Exception Occured While Transferring Money Within Bank", e);
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
			double oldBalance = getCurrentBalance(accountFromTransfer.getAccountNumber());
			double newBalanceOfFromAccount = oldBalance - amountToTransferWithOtherBank;

			boolean isFromAccountBalanceUpdated = updateAccountBalance(connection, accountFromTransfer,
					newBalanceOfFromAccount);

			if (isFromAccountBalanceUpdated) {
				isTransferSuccess = logTransaction(accountFromTransfer, accountNumberToTransfer,
						amountToTransferWithOtherBank, remark, TransactionType.WITHDRAW.getValue(),
						accountFromTransfer.getUserId() + System.currentTimeMillis());
				accountFromTransfer.setBalance(newBalanceOfFromAccount);
				if (isTransferSuccess) {
					connection.commit();
				} else {
					connection.rollback();
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Transferring Money With Other Bank", e);
			throw new CustomException("Exception Occured While Transferring Money With Other Bank", e);
		}
		return isTransferSuccess;
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Transaction Details", e);
			throw new CustomException("Exception Occured While Reterving Transaction Details", e);
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
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Reterving Transaction Details", e);
			throw new CustomException("Exception Occured While Reterving Transaction Details", e);
		}
		return transactionMap;
	}

	@Override
	public List<Transaction> getCustomerTransactions(String accountNumber, String startDate, String endDate, int userId)
			throws CustomException {
		List<Transaction> historyList = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_TRANSACTIONS)) {

			preparedStatement.setString(1, accountNumber);
			preparedStatement.setString(2, startDate);
			preparedStatement.setString(3, endDate);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				historyList = new ArrayList<Transaction>();
				getCustomerTransactionDetail(resultSet, historyList);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "Exception Occured While Reterving Transaction Details", e);
			throw new CustomException("Exception Occured While Reterving Transaction Details", e);
		}
		return historyList;
	}

	private void getCustomerTransactionDetail(ResultSet resultSet, List<Transaction> historyList) throws Exception {
		while (resultSet.next()) {
			historyList.add(getTransactionDetail(resultSet));
		}
	}

	private void getCustomersTransactionDetail(ResultSet resultSet, Map<String, List<Transaction>> transactionList)
			throws Exception {
		while (resultSet.next()) {
			Transaction transaction = getTransactionDetail(resultSet);
			String accountNumber = transaction.getViewerAccount();
			transactionList.computeIfAbsent(accountNumber, k -> new ArrayList<Transaction>()).add(transaction);
		}
	}

	private Transaction getTransactionDetail(ResultSet resultSet) throws Exception {
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
			preparedStatement.setLong(11, System.currentTimeMillis());

			int rowsAffected = preparedStatement.executeUpdate();

			isLoggedSuccessfully = (rowsAffected > 0);

		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Logging The Transaction Details", e);
			throw new CustomException("Exception Occured While Logging The Transaction Details", e);
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
				isBalanceUpdated = true;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Updating Account Balance", e);
			throw new CustomException("Exception Occured While Updating Account Balance", e);
		}
		return isBalanceUpdated;
	}
}
