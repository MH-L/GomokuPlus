package database;

import java.sql.*;
import java.util.ArrayList;

import Model.ServerGame.Move;

public class ConnectionManager {
	public static void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find sql driver.");
		}
		Connection conn = null;
		final String password = "password";
		final String userName = "java";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://104.236.97.57:3306/javabase",
					userName, password);
			System.out.println("Connection succeeded.");
		} catch (SQLException e) {
			System.out.println("DB connection failed.");
			e.printStackTrace();
		}
	}

	public static void insertGameData(ArrayList<Move> moves) {

	}
}
