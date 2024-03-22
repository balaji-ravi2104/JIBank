package com.banking.view;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

import com.banking.utils.CommonUtils.Field;

public class MainView {
	private static final Logger log = Logger.getLogger(MainView.class.getName());
	private Scanner scanner;

	public MainView() {
		this.scanner = new Scanner(System.in);
	}

	public void displayWelcomeMessage() {
		log.info("Welcome to Banking Application!!!");
	}

	public void displayExceptionMessage(Exception e) {
		e.printStackTrace();
		log.warning("An Exception Occured : " + e.getMessage());
	}

	public void displayInputMissMatchMessage() {
		log.info("Please Enter the Valid Input!!");
	}

	public int promptForUserID() throws InputMismatchException {
		System.out.print("Enter your userID: ");
		return scanner.nextInt();
	}

	public String promptForPassword() {
		System.out.print("Enter your password: ");
		return scanner.nextLine();
	}

	public String promptStringInput() {
		return scanner.nextLine();
	}

	public double promptDoubleInput() throws InputMismatchException {
		return scanner.nextDouble();
	}

	public int promptForMainMenuChoice() throws InputMismatchException {
		return scanner.nextInt();
	}

	public void promptNewLine() {
		scanner.nextLine();
	}

	public int promtForIntegerInput() throws InputMismatchException {
		return scanner.nextInt();
	}

	public int promptForAccountNumber() throws InputMismatchException {
		return scanner.nextInt();
	}

	public void displayFieldName(Map<Integer, Field> fieldMap) {
		for (Entry<Integer, Field> entry : fieldMap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
