package database;

import java.sql.*;
import java.util.ArrayList;

import config.ConfHelper;
import model.ServerGame.Move;

public class ConnectionManager {
	public static void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find sql driver.");
		}
		Connection conn = null;
		final String password = ConfHelper.getPassword();
		final String userName = ConfHelper.getDBUsername();
		final String dbName = ConfHelper.getDBName();
		try {
			conn = DriverManager.getConnection("jdbc:mysql://104.236.97.57:3306/" + dbName,
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
