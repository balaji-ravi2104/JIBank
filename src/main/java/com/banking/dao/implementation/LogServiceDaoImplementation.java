package com.banking.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.LogServiceDao;
import com.banking.model.AuditLog;
import com.banking.model.AuditlogActions;
import com.banking.model.SessionDetails;
import com.banking.model.Status;
import com.banking.utils.CustomException;
import com.banking.utils.DatabaseConnection;
import com.banking.utils.LoggerProvider;

public class LogServiceDaoImplementation implements LogServiceDao {

	private static final String LOG_SESSION = "INSERT INTO Session (SessionKey, UserId, LoginTime,UserAgent) VALUES (?,?,?,?)";

	private static final String UPDATE_LOGOUT_SESSION = "UPDATE Session SET LogoutTime = ? WHERE SessionKey = ? AND UserId = ?";

	private static final String AUDIT_LOG = "INSERT INTO AuditLog (TargetID,ActionId,CreatedBy,UserId,Description,StatusId) VALUES (?,?,?,?,?,?);";

	private static final Logger logger = LoggerProvider.getLogger();
	
	public void logAuditTable(AuditLog auditLog) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_LOG)) {
			preparedStatement.setInt(1, auditLog.getTargetId());
			preparedStatement.setInt(2, auditLog.getAuditlogActions().getValue());
			preparedStatement.setLong(3, auditLog.getCreatedTime());
			preparedStatement.setInt(4, auditLog.getUserId());
			preparedStatement.setString(5, auditLog.getDescription());
			preparedStatement.setInt(6, auditLog.getStatus().getValue());

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.log(Level.WARNING,"Exception Occured While Logging Audit Details",e);
			throw new CustomException("Exception Occured While Logging Audit Details", e);
		}
	}

	public void logLoginSession(SessionDetails sessionDetails) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(LOG_SESSION)) {

			preparedStatement.setString(1, sessionDetails.getSessionId());
			preparedStatement.setInt(2, sessionDetails.getUserId());
			preparedStatement.setLong(3, sessionDetails.getLoginTime());
			preparedStatement.setString(4, sessionDetails.getUserAgent());

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				AuditLog auditLog = new AuditLog(sessionDetails.getUserId(), AuditlogActions.LOGIN,
						System.currentTimeMillis(), sessionDetails.getUserId(),
						String.format("User id %d logged into the website", sessionDetails.getUserId()),Status.SUCCESS);
				logAuditTable(auditLog);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.log(Level.WARNING,"Exception Occured While Logging User Session Details",e);
			throw new CustomException("Exception Occured While Logging User Session Details", e);
		}
	}

	public void updateLogoutSession(String SessionKey, int userId) throws CustomException {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LOGOUT_SESSION)) {

			preparedStatement.setLong(1, System.currentTimeMillis());
			preparedStatement.setString(2, SessionKey);
			preparedStatement.setInt(3, userId);
			
			preparedStatement.executeUpdate();
			
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Logging User Session Logout Details",e);
			throw new CustomException("Exception Occured While Logging User Session Logout Details", e);
		}
	}
}
