package com.banking.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.dao.BranchDao;
import com.banking.dao.implementation.BranchDaoImplementation;
import com.banking.utils.CustomException;
import com.banking.utils.LoggerProvider;

public class BranchController {

	private final BranchDao branchDao;
	private static final Logger logger = LoggerProvider.getLogger();

	public BranchController() {
		this.branchDao = new BranchDaoImplementation();
	}

	public boolean isBranchExists(int branchId) throws CustomException {
		boolean isBranchExists = false;
		try {
			isBranchExists = branchDao.checkBranchIdExists(branchId);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception Occured While Checking Branch Details",e);
			throw new CustomException("Exception Occured While Checking Branch Details", e);
		}
		return isBranchExists;
	}

}
