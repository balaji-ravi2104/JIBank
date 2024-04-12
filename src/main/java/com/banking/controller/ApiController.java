package com.banking.controller;

import java.util.Map;

import com.banking.dao.ApiDao;
import com.banking.dao.implementation.ApiDaoImplementation;
import com.banking.model.Token;
import com.banking.utils.CustomException;
import com.banking.utils.TokenGenerator;

public class ApiController {
	private ApiDao apiDao;
	
	public ApiController() {
		this.apiDao = new ApiDaoImplementation();
	}
	
	public boolean createApikey(int userId) throws CustomException {
		boolean isKeyCreated = false;
		try {
			String apiToken = TokenGenerator.generateToken(32);
			long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L;
			isKeyCreated = apiDao.createApikey(userId,apiToken,System.currentTimeMillis(),System.currentTimeMillis()+threeDaysInMillis);
		}catch (Exception e) {
			throw new CustomException("Error While Creating Api Token",e);
		}
		return isKeyCreated;
	}

	public Map<Integer, Token> getApiKeys(int userId) throws CustomException{
		Map<Integer, Token> userApiKeys = null;
		try {
			userApiKeys = apiDao.getApiKeys(userId);
		}catch (Exception e) {
			throw new CustomException("Error While Getting Api Token",e);
		}
		return userApiKeys;
	}
}
