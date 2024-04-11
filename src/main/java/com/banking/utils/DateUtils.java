package com.banking.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {


	public static String longToDate(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime time = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
 
		return time.format(formatter);
	}

	public static String formateLongToDate(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime time = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd - hh:mm:ss");

		return time.format(formatter);
	}

	public static LocalDate formatDateString(String dateString) throws CustomException {
		InputValidator.isNull(dateString, "Date of birth Cannot be Null!!");
//		if (!InputValidator.validateDateOfBirth(dateString)) {
//			userView.displayInvalidDateOfBirth();
//			return null;
//		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(dateString, formatter);
		return date;

	}

	public static long formatDate(LocalDate date) throws CustomException {
		InputValidator.isNull(date, "Date Cannot be Null!!");
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

	}

	public static String convertToHtmlDateFormat(String dobFromDatabase) {
		String[] parts = dobFromDatabase.split("/");
		if (parts.length != 3) {
			return "";
		}
		return parts[0] + "-" + parts[1] + "-" + parts[2];
	}
}
