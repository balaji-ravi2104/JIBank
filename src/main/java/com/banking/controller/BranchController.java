package com.banking.controller;

import java.util.logging.Logger;

import com.banking.dao.BranchDao;
import com.banking.dao.implementation.BranchDaoImplementation;
import com.banking.utils.CustomException;

public class BranchController {

	private final BranchDao branchDao;
	private static final Logger log = Logger.getLogger(BranchController.class.getName());

	public BranchController() {
		this.branchDao = new BranchDaoImplementation();
	}

	public boolean isBranchExists(int branchId) throws CustomException {
		boolean isBranchExists = false;
		try {
			isBranchExists = branchDao.checkBranchIdExists(branchId);
		} catch (Exception e) {
			throw new CustomException("Error while Checking Branch Details!!", e);
		}
		return isBranchExists;
	}

	public boolean validateBranchId(int branchId) throws CustomException {
		boolean isValidBranchId = isBranchExists(branchId);
		if (!isValidBranchId) {
			log.warning("Invalid Branch Id!!!");
		}
		return isValidBranchId;
	}

}
