package com.banking.https;

import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HttpGetExample {
	private static final String GET_USER_URL = "https://localhost:8443/JIBank/api/user/1013";
	private static final String AUTH_TOKEN = "Ct40cqqcv2VIBIw1pJ1gan9l5zmIYFIb";
	private static final String USER = "1001";

	public static void main(String[] args) {
		try {
			System.setProperty("javax.net.ssl.trustStore", "/home/bala-pt-7360/Documents/keystore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "Balaji@123");
			
			URL urlObj = new URL(GET_USER_URL);
			HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", AUTH_TOKEN);
			connection.setRequestProperty("accessUserId", USER);

			int responseCode = connection.getResponseCode();
			System.out.println("Response Code :" + responseCode);

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				StringBuilder sb = new StringBuilder();
				Scanner scanner = new Scanner(connection.getInputStream());
				while (scanner.hasNext()) {
					sb.append(scanner.next());
				}
				System.out.println(sb);

				scanner.close();
			} else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
				System.out.println("Invalid Token");
			} else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
				System.out.println("No Account Found");
			} else {
				System.out.println("Error: Unexpected response code " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
