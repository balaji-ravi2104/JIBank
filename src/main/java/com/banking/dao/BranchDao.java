package com.banking.dao;

import com.banking.utils.CustomException;

public interface BranchDao {

	boolean checkBranchIdExists(int branchId) throws CustomException;

}
