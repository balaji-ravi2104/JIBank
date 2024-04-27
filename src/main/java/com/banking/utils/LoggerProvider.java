package com.banking.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerProvider {

	private static Logger logger;

	public static Logger getLogger() {
		if (logger != null) {
			return logger;
		}
		synchronized (LoggerProvider.class) {
			if (logger != null) {
				return logger;
			}
			try {
				logger = Logger.getLogger("Bank_Logger");
				FileHandler fileHandler = new FileHandler(
						"/home/bala-pt-7360//eclipse-workspace/JIBank/logs/jiBankLogger.txt", true);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
				logger.setLevel(Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

}
