package database;

import java.sql.*;

public class ConnectionManager {
	public static void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find sql driver.");
		}
		Connection conn = null;
		final String password = "";
		final String userName = "root";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://104.236.97.57:3306/",
					userName, password);
		} catch (SQLException e) {
			System.out.println("DB connection failed.");
			e.printStackTrace();
		}
	}
}
