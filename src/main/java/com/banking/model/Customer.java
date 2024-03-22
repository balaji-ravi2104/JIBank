	package com.banking.model;

import java.io.Serializable;

public class Customer extends User implements Serializable{
	private static final long serialVersionUID = 1L;
	String panNumber;
	String aadharNumber;

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	@Override
	public String toString() {
		return "Customer [panNumber=" + panNumber + ", aadharNumber=" + aadharNumber + "]";
	}

}
