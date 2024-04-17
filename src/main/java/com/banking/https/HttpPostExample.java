package com.banking.https;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class HttpPostExample {

	private static final String LOGIN_URL = "https://localhost:8443/JIBank/api/login";

	public static void main(String[] args) {
		try {
			System.setProperty("javax.net.ssl.trustStore", "/home/bala-pt-7360/Documents/keystore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "Balaji@123");
			
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userId", "1001");
			jsonObject.put("password", "Balaji@123");
			String requestBody = jsonObject.toString();

			URL url = new URL(LOGIN_URL);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
				dos.writeBytes(requestBody);
				dos.flush();
			}

			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				try (BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String line;
					// StringBuilder response = new StringBuilder();
					while ((line = bf.readLine()) != null) {
						System.out.println(line);
						// response.append(line);
					}
					// System.out.println("Response: " + response.toString());
				}
			} else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
				System.out.println("Invalid User Id or Password.");
			} else {
				System.out.println("Error: Unexpected response code " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
