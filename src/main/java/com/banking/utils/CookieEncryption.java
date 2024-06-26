package com.banking.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CookieEncryption {
	private static final String AES_KEY = "AYWB1730azhb07FU"; // 128-bit key (16 bytes)

	public static String encrypt(String plaintext) throws CustomException {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			throw new CustomException("Exception Occured While Encrypting", e);
		}
	}

	public static String decrypt(String ciphertext) throws CustomException {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
			return new String(decryptedBytes);
		} catch (Exception e) {
			throw new CustomException("Exception Occured While Decrypting", e);
		}
	}

//	public static void main(String[] args) throws Exception {
//		String plaintext = "My name is Balaji";
//		String encrypted = encrypt(plaintext);
//		System.out.println("Encrypted cookie value: " + encrypted);
//		String decrypted = decrypt(encrypted);
//		System.out.println("Decrypted cookie value: " + decrypted);
//	}
}
