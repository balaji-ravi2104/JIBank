package com.banking.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class InputValidator {

	private static final Pattern PASSWORD_VALIDATE_PATTERN = Pattern
			.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,}$");
	private static final Pattern EMAIL_VALIDATE_PATTERN = Pattern
			.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
	private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^[7-9]{1}[0-9]{9}$");

	private static final Pattern DATE_OF_BIRTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
	String dobPattern = "^\\d{4}-\\d{2}-\\d{2}$";

	private static final Pattern PAN_NUMBER_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

	private static final Pattern AADHAR_NUMBER_PATTERN = Pattern.compile("\\d{4}\\s\\d{4}\\s\\d{4}");

	public static void isNull(Object object, String ErrorMessage) throws CustomException {
		if (object == null) {
			throw new CustomException(ErrorMessage);
		}
	}

	public static boolean validatePassword(String password) throws CustomException, PatternSyntaxException {
		isNull(password, ErrorMessages.INPUT_NULL_MESSAGE);
		return PASSWORD_VALIDATE_PATTERN.matcher(password).matches();
	}

	public static boolean validateEmail(String email) throws CustomException, PatternSyntaxException {
		isNull(email, ErrorMessages.INPUT_NULL_MESSAGE);
		return EMAIL_VALIDATE_PATTERN.matcher(email).matches();
	}

	public static boolean validateMobileNumber(String mobileNumber) throws CustomException, PatternSyntaxException {
		isNull(mobileNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		return MOBILE_NUMBER_PATTERN.matcher(mobileNumber).matches();
	}

	public static boolean validateDateOfBirth(String dateOfBirth) throws CustomException, PatternSyntaxException {
		isNull(dateOfBirth, ErrorMessages.INPUT_NULL_MESSAGE);
		return DATE_OF_BIRTH_PATTERN.matcher(dateOfBirth).matches();
	}

	public static boolean validateString(String userName) throws CustomException {
		isNull(userName, ErrorMessages.INPUT_NULL_MESSAGE);
		return userName.isEmpty();
	}

	public static boolean validatePanNumber(String panNumber) throws CustomException {
		isNull(panNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		return PAN_NUMBER_PATTERN.matcher(panNumber).matches();
	}

	public static boolean validateAadharNumber(String aadharNumber) throws CustomException {
		isNull(aadharNumber, ErrorMessages.INPUT_NULL_MESSAGE);
		return AADHAR_NUMBER_PATTERN.matcher(aadharNumber).matches();
	}

	public static boolean validateAccountStatus(String fieldValue) throws CustomException {
		isNull(fieldValue, ErrorMessages.INPUT_NULL_MESSAGE);
		return fieldValue.isEmpty();
	}

	public static boolean validateBalance(double balance) {
		return balance < 0;
	}

}
