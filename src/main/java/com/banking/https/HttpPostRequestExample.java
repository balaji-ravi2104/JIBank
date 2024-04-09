package com.banking.https;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpPostRequestExample {

	public static void main(String[] args) {

		HashMap<String, Object> carAttributes = new HashMap<String, Object>();
		carAttributes.put("year", 2019);
		carAttributes.put("price", 500000.00);
		carAttributes.put("color", "red");

		Car car = new Car("BMW X-5", carAttributes);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(car);
			
			URL url = new URL("https://api.restful-api.dev/objects");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");

			try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
				dos.writeBytes(requestBody);
			}
			System.out.println("Response Code :" + conn.getResponseCode());

			try (BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
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
