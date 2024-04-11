package com.banking.https;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpGetExample {
	private static final String GET_USER_URL = "http://localhost:8080/JIBank/api/user/1012";
	private static final String AUTH_TOKEN = "mZn1PxyB9zRtdsJQc4W6Kjl3Ah7wX2EF";
	private static final String USER = "1001";

	public static void main(String[] args) {
		try {
			URL urlObj = new URL(GET_USER_URL);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", AUTH_TOKEN);
			connection.setRequestProperty("accessUserId", USER);

			int responseCode = connection.getResponseCode();
			System.out.println("Response Code :" + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {

				StringBuilder sb = new StringBuilder();
				Scanner scanner = new Scanner(connection.getInputStream());
				while (scanner.hasNext()) {
					sb.append(scanner.next());
				}
				System.out.println(sb);

				scanner.close();
			} else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				System.out.println("Invalid Token");
			} else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
				System.out.println("No Account Found");
			} else {
				System.out.println("Error: Unexpected response code " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
