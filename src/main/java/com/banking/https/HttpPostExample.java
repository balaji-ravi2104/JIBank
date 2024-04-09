package com.banking.https;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostExample {
	
	private static final String LOGIN_URL = "http://localhost:8080/JIBank/account/getAccounts";
	private static final String POST_PARAMS = "userId=1008&branchId=3007";

	public static void main(String[] args) {
		try {
			URL url = new URL(LOGIN_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
				dos.writeBytes(POST_PARAMS);
			}

			System.out.println("Response Code :" + connection.getResponseCode());

			try (BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = bf.readLine()) != null) {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
