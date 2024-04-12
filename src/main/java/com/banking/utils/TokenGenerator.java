package com.banking.utils;

import java.security.SecureRandom;

public class TokenGenerator {
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String generateToken(int length) {
		SecureRandom random = new SecureRandom();
		StringBuilder token = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomChar = CHARACTERS.charAt(randomIndex);
			token.append(randomChar);
		}
		return token.toString();
	}
}
