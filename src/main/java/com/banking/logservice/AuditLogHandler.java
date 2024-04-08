package com.banking.logservice;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.banking.model.AuditlogActions;
import com.banking.model.SessionDetails;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;

public class AuditLogHandler {
	private static final String LOG_SESSION = "INSERT INTO Session (SessionKey, UserId, LoginTime,UserAgent) VALUES (?,?,?,?)";
	
	private static final String UPDATE_LOGOUT_SESSION = "UPDATE Session SET LogoutTime = ? WHERE SessionKey = ? AND UserId = ?";
	
	private static final String AUDIT_LOG = "INSERT INTO AuditLog (TargetID,ActionId,CreatedBy,UserId,Description) VALUES (?,?,?,?,?);";

	public boolean logLoginSession(SessionDetails sessionDetails) throws CustomException {
		boolean isSessionLogged = false;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(LOG_SESSION)) {

			preparedStatement.setString(1, sessionDetails.getSessionId());
			preparedStatement.setInt(2, sessionDetails.getUserId());
			preparedStatement.setLong(3, sessionDetails.getLoginTime());
			preparedStatement.setString(4, sessionDetails.getUserAgent());

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				isSessionLogged = true;
				logAuditTable(sessionDetails.getUserId(), AuditlogActions.LOGIN.getValue(), System.currentTimeMillis(),
						sessionDetails.getUserId(),
						String.format("User id %d logged into the website", sessionDetails.getUserId()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Error While Logging User Session Details");
		}
		return isSessionLogged;
	}

	public boolean updateLogoutSession(String SessionKey, int userId) throws CustomException {
		boolean isSessionUpdated = false;
		System.out.println(SessionKey + " " + userId);
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LOGOUT_SESSION)) {

			preparedStatement.setLong(1, System.currentTimeMillis());
			preparedStatement.setString(2, SessionKey);
			preparedStatement.setInt(3, userId);

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				isSessionUpdated = true;
				logAuditTable(userId, AuditlogActions.LOGOUT.getValue(), System.currentTimeMillis(), userId,
						String.format("User id %d logged out the website", userId));
			}

		} catch (Exception e) {
			throw new CustomException("Error While Logging User Session Details");
		}
		return isSessionUpdated;
	}

	public void logAuditTable(int targetId, int actionId, long createdTime, int userId, String description)
			throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_LOG)) {
			preparedStatement.setInt(1, targetId);
			preparedStatement.setInt(2, actionId);
			preparedStatement.setLong(3, createdTime);
			preparedStatement.setInt(4, userId);
			preparedStatement.setString(5, description);

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Error While Logging Audit Details");
		}
	}
}
