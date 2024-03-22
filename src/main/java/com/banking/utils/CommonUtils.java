package com.banking.utils;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
	public enum Field {
		FirstName, LastName, Gender, Email, ContactNumber, Address, DateOfBirth, Pan, Aadhar,Status
	}

	public static Map<Integer, Field> generateFieldMap() {
		Map<Integer, Field> fieldMap = new HashMap<>();
		int key = 1;
		for (Field field : Field.values()) {
			fieldMap.put(key++, field);
		}
		return fieldMap;
	}
}
