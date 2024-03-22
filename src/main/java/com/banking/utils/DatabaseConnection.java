package com.banking.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String url = "jdbc:mysql://localhost:3306/Banking_Application";
	private static final String userName = "root";
	private static final String password = "root";

	public static Connection getConnection() throws CustomException {
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, userName, password);
		} catch (SQLException | ClassNotFoundException e ) {
			throw new CustomException("An Error Occured During the Connection Creation",e);
		} 
		return connection;
	}
}
