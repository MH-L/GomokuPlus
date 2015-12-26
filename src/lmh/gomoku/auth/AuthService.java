package lmh.gomoku.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.RegistrationException;
import lmh.gomoku.util.HashHelper;

/**
 * Authentication service for the gomoku game.
 * Date: 2015/11/11
 * @author Minghao
 */
public class AuthService {
	public static final int PORT = 1993;
	private static ConnectionManager manager;
	private static int threadCount = 0;
	private static Thread cron;

	public static void main(String[] args) {
		manager = ConnectionManager.getInstance();
		initializeCron();
		try {
			ServerSocket ss = new ServerSocket(PORT);
			while (true) {
				Socket clientSocket = ss.accept();
				authenticate(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Cannot bind to the port. Port could be in use.");
			System.exit(1);
		}
	}

	private static void initializeCron() {
		cron = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public static void authenticate(Socket clientSocket) throws IOException {
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	/**
	 * Checks if a password matches our standard. A password has to contain at least one
	 * letter, one digit, one special character and with length between 8 and 17.
	 * @param password the password to check
	 * @return
	 */
	private static boolean verifyPass(String password) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,17}$";
		return password.matches(regex);
	}

	/**
	 * Checks if a username matches our standard. A username is valid if and only if
	 * it contains only upper/lowercase letters, numbers and underscores.
	 * @param username
	 * @return
	 */
	private static boolean verifyUsername(String username) {
		String regex = "^[a-zA-Z0-9]{4,16}$";
		return username.matches(regex);
	}

	public static void createAccountRawStrings(String usernameRaw, String passwordRaw, String invitationCodeRaw) {

	}

	/**
	 * Generates today's key for encrypting username, password and credential.
	 * @return
	 */
	private static String generateTodaysKey() {
		return null;
	}

	/**
	 * Creates an account for the specified username with the given password. An account will
	 * be created only if 1) username and password are both valid. 2) credential matches record
	 * in the database and is not taken.
	 * @param username
	 * @param password
	 * @param invitationCode
	 * @return true if account creation success and false otherwise
	 * @throws RegistrationException If the account cannot be registered.
	 */
	public static void createAccount(String username, String password, String invitationCode)
			throws RegistrationException {
		if (!(AuthService.verifyPass(password) && AuthService.verifyUsername(username)))
			throw new RegistrationException("Username or password is invalid.");

		Connection conn = manager.getActiveConnection();
		String query = String.format("SELECT username FROM Credentials WHERE credential='%s';", username);
		Statement stment = null;
		boolean invitationValid = false;
		HashHelper hashInst = HashHelper.getInstance();
		String encryptedPassword = new String(hashInst.encrypt(password));
		try {
			stment = conn.createStatement();
			ResultSet result = stment.executeQuery(query);
			if (result.next()) {
				invitationValid = true;
				String credentialUName = result.getString("username");
				// Credential taken if username not empty string
				if (!credentialUName.equals(""))
					throw new RegistrationException("The invitation"
							+ " code has already been taken!");
			}
			if (!invitationValid)
				throw new RegistrationException("Invalid invitation code.");
			query = String.format("UPDATE Credentials SET username='%s', "
					+ "password='%s' where credential='%s'",
					username, encryptedPassword, invitationCode);
			stment.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
