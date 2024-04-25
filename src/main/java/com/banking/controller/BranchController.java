package com.banking.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.BranchDao;
import com.banking.utils.CustomException;
import com.banking.utils.LoggerProvider;

public class BranchController {

	private BranchDao branchDao;
	private static final Logger logger = LoggerProvider.getLogger();

	public BranchController() {
		try {
			Class<?> clazz = Class.forName("com.banking.dao.implementation.BranchDaoImplementation");
			this.branchDao = (BranchDao) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public boolean isBranchExists(int branchId) throws CustomException {
		boolean isBranchExists = false;
		try {
			isBranchExists = branchDao.checkBranchIdExists(branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception Occured While Checking Branch Details", e);
			throw new CustomException("Exception Occured While Checking Branch Details", e);
		}
		return isBranchExists;
	}

}
