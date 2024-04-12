package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.banking.dao.ApiDao;
import com.banking.model.Token;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;

public class ApiDaoImplementation implements ApiDao {

	private static final String CREATE_API_KEY = "INSERT INTO TokenDB (UserId,token,createdDate,validUpto) VALUES (?,?,?,?);";
	private static final String GET_API_KEYS = "SELECT * FROM TokenDB WHERE UserId = ?";

	@Override
	public boolean createApikey(int userId, String apiToken, long createdTime, long validUpto) throws CustomException {
		boolean isKeyCreated = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CREATE_API_KEY)) {
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, apiToken);
			preparedStatement.setLong(3, createdTime);
			preparedStatement.setLong(4, validUpto);

			int roesAffected = preparedStatement.executeUpdate();
			isKeyCreated = (roesAffected > 0);
		} catch (Exception e) {
			throw new CustomException("Error While Creating API Key", e);
		}
		return isKeyCreated;
	}

	@Override
	public Map<Integer, Token> getApiKeys(int userId) throws CustomException {
		Map<Integer, Token> userApiKeys = null;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_API_KEYS)) {
			preparedStatement.setInt(1, userId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				userApiKeys = new HashMap<Integer, Token>();
				reteriveApiKeys(resultSet,userApiKeys);
			}
		} catch (Exception e) {
			throw new CustomException("Error While Creating API Key", e);
		}
		return userApiKeys;
	}

	private void reteriveApiKeys(ResultSet resultSet, Map<Integer, Token> userApiKeys) throws SQLException {
		Token token;
		while(resultSet.next()) {
			token = new Token();
			getTokenObject(resultSet,token);
			userApiKeys.put(token.getTokenId(), token);
		}
	}

	private void getTokenObject(ResultSet resultSet, Token token) throws SQLException {
		token.setTokenId(resultSet.getInt(1));
		token.setUserId(resultSet.getInt(2));
		token.setToken(resultSet.getString(3));
		token.setCreatedTime(resultSet.getLong(4));
		token.setValidUpto(resultSet.getLong(5));
		token.setTokenStatus(resultSet.getInt(6));
	}
}
