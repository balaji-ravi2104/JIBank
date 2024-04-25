package com.banking.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.ApiDao;
import com.banking.dao.implementation.ApiDaoImplementation;
import com.banking.logservice.AuditLogHandler;
import com.banking.model.AuditLog;
import com.banking.model.AuditlogActions;
import com.banking.model.Status;
import com.banking.model.Token;
import com.banking.utils.CustomException;
import com.banking.utils.LoggerProvider;
import com.banking.utils.TokenGenerator;

public class ApiController {
	private ApiDao apiDao;
	private AuditLogHandler auditLogHandler;

	private static final Logger logger = LoggerProvider.getLogger();
	
	public ApiController() {
		this.apiDao = new ApiDaoImplementation();
		this.auditLogHandler = new AuditLogHandler();
	}

	public boolean createApikey(int userId, int creatingUserId) throws CustomException {
		boolean isKeyCreated = false;
		try {
			String apiToken = TokenGenerator.generateToken(32);
			long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L;
			isKeyCreated = apiDao.createApikey(userId, apiToken, System.currentTimeMillis(),
					System.currentTimeMillis() + threeDaysInMillis);
			if(isKeyCreated) {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Created API keys for User Id %d", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}else {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.CREATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Try to Create API keys of User Id %d But Failed", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Creating Api Key",e);
			throw new CustomException("Exception Occured While Creating Api Key", e);
		}
		return isKeyCreated;
	}

	public Map<Integer, Token> getApiKeys(int userId, int creatingUserId) throws CustomException {
		Map<Integer, Token> userApiKeys = null;
		try {
			userApiKeys = apiDao.getApiKeys(userId);
			AuditLog auditLog = new AuditLog(userId, AuditlogActions.VIEW, System.currentTimeMillis(),
					creatingUserId,
					String.format("User id %d View the API keys of User Id %d", creatingUserId, userId),
					Status.SUCCESS);

			auditLogHandler.addAuditData(auditLog);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Getting Api Key",e);
			throw new CustomException("Exception Occured While Getting Api Key", e);
		}
		return userApiKeys;
	}

	public boolean updateApikey(int tokenId, int creatingUserId,int userId) throws CustomException {
		boolean isKeyUpdated = false;
		try {
			String apiToken = TokenGenerator.generateToken(32);
			long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L;
			isKeyUpdated = apiDao.updateApiKey(tokenId, apiToken, System.currentTimeMillis(),
					System.currentTimeMillis() + threeDaysInMillis);
			if(isKeyUpdated) {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Updated API keys for User Id %d", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}else {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Try to Updated API keys for User Id %d But Failed", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Updating Api Key",e);
			throw new CustomException("Exception Occured While Updating Api Key", e);
		}
		return isKeyUpdated;

	}

	public boolean deleteApikey(int tokenId, int creatingUserId,int userId) throws CustomException {
		boolean isKeyDeleted = false;
		try {
			isKeyDeleted = apiDao.deleteApiKey(tokenId);
			if(isKeyDeleted) {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Deleted API keys for User Id %d", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}else {
				AuditLog auditLog = new AuditLog(userId, AuditlogActions.UPDATE, System.currentTimeMillis(),
						creatingUserId,
						String.format("User id %d Try to Delete API keys for User Id %d But Failed", creatingUserId, userId),
						Status.SUCCESS);

				auditLogHandler.addAuditData(auditLog);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Deleting Api Key",e);
			throw new CustomException("Exception Occured While Deleting Api Key", e);
		}
		return isKeyDeleted;
	}
}
