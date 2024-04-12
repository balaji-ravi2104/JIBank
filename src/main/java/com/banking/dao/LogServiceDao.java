package com.banking.dao;

import com.banking.model.AuditLog;
import com.banking.model.SessionDetails;
import com.banking.utils.CustomException;

public interface LogServiceDao {

	void logAuditTable(AuditLog auditLog) throws CustomException;
	
	void logLoginSession(SessionDetails sessionDetails) throws CustomException;
	
	void updateLogoutSession(String SessionKey, int userId) throws CustomException;
}
