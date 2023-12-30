package javacode.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    
	static private final String jdbcUrl = "jdbc:sqlite:src\\resources\\Wildfires.db";

	public static Connection getConnection() {

		try {
			Connection connection = DriverManager.getConnection(jdbcUrl);
			return connection;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot get a connection " + jdbcUrl, e);
		}
	}
}
