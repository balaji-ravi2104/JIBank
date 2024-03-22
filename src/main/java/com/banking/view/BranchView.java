package com.banking.view;

import java.util.logging.Logger;

public class BranchView {

	private static final Logger log = Logger.getLogger(BranchView.class.getName());

	public void displayInvalidBranchMessage() {
		log.info("Invalid BranchId!!");
	}

}
