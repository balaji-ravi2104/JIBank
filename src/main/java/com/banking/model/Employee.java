package com.banking.model;

import java.io.Serializable;

public class Employee extends User implements Serializable{
	private static final long serialVersionUID = 1L;
	int branchId;

	@Override
	public String toString() {
		return "Employee [branchId=" + branchId + "]";
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

}
