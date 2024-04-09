package com.banking.https;

import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HttpGetRequestExample {

	public static void main(String[] args) {
		try {
			URL urlObj = new URL("https://restcountries.com/v3.1/name/Italy");
			HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");

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
			} else {
				System.out.println("GET request did not work.");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
