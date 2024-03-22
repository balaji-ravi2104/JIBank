package com.banking.utils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGenerator {
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	private static final String NUMBER = "0123456789";
	private static final String OTHER_CHAR = "!@#$%&*()_+-=[]|,./?><";
	private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
	private static final SecureRandom random = new SecureRandom();

	public static String generatePassword() {
		return generatePassword(8);
	}

	private static String generatePassword(int length) {
		StringBuilder password = new StringBuilder(length);

		password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
		password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
		password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
		password.append(OTHER_CHAR.charAt(random.nextInt(OTHER_CHAR.length())));

		String remainingChars = PASSWORD_ALLOW_BASE;

		for (int i = 4; i < length; i++) {
			int randomCharIndex = random.nextInt(remainingChars.length());
			password.append(remainingChars.charAt(randomCharIndex));
		}

		List<Character> charList = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(charList);
		StringBuilder shuffledPassword = new StringBuilder(charList.size());
		charList.forEach(shuffledPassword::append);

		return shuffledPassword.toString();
	}

}
