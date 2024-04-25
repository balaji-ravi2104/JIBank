package com.banking.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerProvider {

	static Logger gLogger;
	public static Logger getLogger() {
		if(gLogger != null) {
			return gLogger;
		}
		Logger logger = null;
		synchronized (LoggerProvider.class) {
			if(gLogger != null) {
				return gLogger;
			}
			try {
				logger = Logger.getLogger("Bank_Logger");
				FileHandler fileHandler = new FileHandler("/home/bala-pt-7360//eclipse-workspace/JIBank/logs/jiBankLogger.txt", true);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
				logger.setLevel(Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return gLogger = logger;
	}

}
