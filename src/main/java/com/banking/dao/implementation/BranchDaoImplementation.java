package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.banking.dao.BranchDao;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;

public class BranchDaoImplementation implements BranchDao {
	private static final String BRANCH_ID_EXISTS = "SELECT COUNT(*) FROM Branches WHERE branchid = ?";

	@Override
	public boolean checkBranchIdExists(int branchId) throws CustomException {
		boolean isValidId = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(BRANCH_ID_EXISTS)) {

			preparedStatement.setInt(1, branchId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					isValidId = count > 0;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error While Checking Branch Details!!", e);
		}
		return isValidId;
	}

}
