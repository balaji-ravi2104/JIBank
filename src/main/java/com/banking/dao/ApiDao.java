package com.banking.dao;

import java.util.Map;

import com.banking.model.Token;
import com.banking.utils.CustomException;

public interface ApiDao {
	
	boolean createApikey(int userId, String apiToken, long createdTime, long validUpto) throws CustomException;

	Map<Integer, Token> getApiKeys(int userId) throws CustomException;

	boolean updateApiKey(int tokenId, String apiToken, long createdTime, long validUpto) throws CustomException;

	boolean deleteApiKey(int tokenId) throws CustomException;
}
