package lmh.gomoku.database;

import java.sql.*;
import java.util.ArrayList;

import lmh.gomoku.auth.AuthService;
import lmh.gomoku.config.ConfHelper;
import lmh.gomoku.model.ServerGame.Move;
import lmh.gomoku.util.HashHelper;

public class ConnectionManager {
	private Connection conn = null;
	private ConfHelper conf;
	private static ConnectionManager instance = null;

	private ConnectionManager() {
		connect();
		conf = ConfHelper.getInstance();
	}

	public ConnectionManager getInstance() {
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
		final String password = conf.getPassword();
		final String userName = conf.getDBUsername();
		final String dbName = conf.getDBName();
		try {
			conn = DriverManager.getConnection("jdbc:mysql://104.236.97.57:3306/" + dbName,
					userName, password);
			System.out.println("Connection succeeded.");
		} catch (SQLException e) {
			System.out.println("DB connection failed.");
			e.printStackTrace();
		}
	}

	public static void insertGameData(ArrayList<Move> moves, long curmillis,
			int player1ID, int player2ID) {
		String gameHash = getGameHash(curmillis, player1ID, player2ID);
		String query = String.format("INSERT INTO Game (gameHash, fileName) VALUES"
				+ " ('%s', 'Rec-%s')", gameHash, gameHash);
	}

	public boolean createAccount(String username, String password, String invitationCode) {
		/**
		 * First check that the username is valid (i.e. first of all it does not
		 * have any invalid characters, and the database does not have that record)
		 */
		if (!(AuthService.verifyPass(password) && AuthService.verifyUsername(username)))
			return false;

		String query = String.format("SELECT * FROM Credentials WHERE credential='%s';", username);
		Statement stment = null;
		boolean invitationValid = false;
		HashHelper hashInst = HashHelper.getInstance();
		String encryptedPassword = new String(hashInst.encrypt(password));
		try {
			stment = conn.createStatement();
			ResultSet result = stment.executeQuery(query);
			if (result.next())
				invitationValid = true;
			if (!invitationValid)
				return false;
			query = String.format("INSERT INTO Credentials (username, userID) VALUES ('%s', '%s');",
					username, encryptedPassword);
			stment.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static String getGameHash(long curmillis, int player1ID, int player2ID) {
		String tohash = String.valueOf(curmillis) + player1ID;
		tohash += player2ID;
		HashHelper hashInstance = HashHelper.getInstance();
		return new String(hashInstance.encrypt(tohash));
	}
}
