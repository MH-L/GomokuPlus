package database;

import java.sql.*;
import java.util.ArrayList;

import util.HashHelper;
import config.ConfHelper;
import model.ServerGame.Move;

public class ConnectionManager {
	private Connection conn = null;
	private static ConnectionManager instance = null;

	private ConnectionManager() {
		connect();
	}

	private ConnectionManager getInstance() {
		if (instance == null) {
			return new ConnectionManager();
		} else {
			return instance;
		}
	}

	private void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find sql driver.");
		}
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

	public static void createAccount(String username, String password) {
		/**
		 * First check that the username is valid (i.e. first of all it does not
		 * have any invalid characters, and the database does not have that record)
		 */
		String query = String.format("SELECT FROM Credentials WHERE username=%s;", username);
		HashHelper hashInst = HashHelper.getInstance();
		String encryptedPassword = new String(hashInst.encrypt(password));
		query = String.format("INSERT INTO Credentials (username, userID) VALUES (%s, %s);",
				username, encryptedPassword);
	}

	public static String getGameHash(int curmillis, int player1ID, int player2ID) {
		String tohash = String.valueOf(curmillis) + player1ID;
		tohash += player2ID;
		HashHelper hashInstance = HashHelper.getInstance();
		return new String(hashInstance.encrypt(tohash));
	}
}
