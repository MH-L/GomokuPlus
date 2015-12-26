package lmh.gomoku.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.RegistrationException;
import lmh.gomoku.util.HashHelper;

/**
 * Authentication service for the gomoku game.
 * Date: 2015/11/11
 * @author Minghao
 */
public class AuthService {
	private static final int KEY_SIZE = 128;
	public static final int PORT = 1993;
	private static ConnectionManager manager;
	private static int threadCount = 0;
	private static Thread cron;
	private static String key;
	private static long keyLastGenerated;
	private static final int keyGenerationIntervalMillis = 1800000; // 30 mins

	public static void main(String[] args) {
		manager = ConnectionManager.getInstance();
		initializeCron();
		key = generateTodaysKey();
		keyLastGenerated = System.currentTimeMillis();

		try {
			ServerSocket ss = new ServerSocket(PORT);
			while (true) {
				Socket clientSocket = ss.accept();
				initializeAuthThread(clientSocket);
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
					if (System.currentTimeMillis() - keyLastGenerated >
						keyGenerationIntervalMillis) {
						key = generateTodaysKey();
						keyLastGenerated = System.currentTimeMillis();
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private static void initializeAuthThread(Socket clientSocket) {
		Thread authThread = new Thread() {
			@Override
			public void run() {
				try {
					PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader reader = new BufferedReader
							(new InputStreamReader(clientSocket.getInputStream()));

					String line = reader.readLine();
					if (line.startsWith(AuthConstants.INT_CREATE_ACCOUNT + ",")) {
						// Set limit to negative because the passed in username, password
						// or credential could all be empty string
						String[] keyInfo = line.split(",", -1);
						String rawUsername = keyInfo[1];
						String rawPassword = keyInfo[2];
						String rawCredential = keyInfo[3];
						try {
							createAccountRawStrings(rawUsername, rawPassword, rawCredential);
						} catch (RegistrationException e) {
							writer.println(AuthConstants.INT_GENERAL_ERROR + "," + e.getMessage());
						}
					} else if (line.startsWith(AuthConstants.INT_CHANGE_UNAME + ",")) {

					} else if (line.startsWith(AuthConstants.INT_CHANGE_PASS + ",")) {

					} else if (line.startsWith(AuthConstants.INT_LOG_IN_PASSWORD + ",")) {
						String[] keyInfo = line.split(",", -1);
						String username = keyInfo[1];
						String password = keyInfo[2];
					} else if (line.startsWith(AuthConstants.INT_LOG_IN_TOKEN + ",")) {

					} else {
						// unknown request.
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};

		authThread.start();
	}

	public static void authenticate(Socket clientSocket) throws IOException {

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

	public static void createAccountRawStrings(String usernameRaw,
			String passwordRaw, String invitationCodeRaw) throws RegistrationException {
		String username = new String(Base64.getDecoder().decode(usernameRaw));
		String password = new String(Base64.getDecoder().decode(passwordRaw));
		String invitationCode = new String(Base64.getDecoder().decode(invitationCodeRaw));
		createAccount(username, password, invitationCode);
	}

	/**
	 * Generates today's key for encrypting username, password and credential.
	 * Key is of size 128;
	 * @return
	 */
	private static String generateTodaysKey() {
		String allPossible = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rand = new Random();
		String candidate = "";
		for (int i = 0; i < KEY_SIZE; i++) {
			char ch = allPossible.charAt(rand.nextInt(allPossible.length()));
			candidate += ch;
		}
		return candidate;
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

	private static void userLoginRawStrings(String username, String password) {

	}

	private static String generateTokenString(String username, String password) {
		SecretKeySpec specKey = new SecretKeySpec(key.getBytes(), "AES");
		try {
			Cipher cp = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			cp.init(Cipher.ENCRYPT_MODE, specKey);
			byte[] cipherUsername = new byte[cp.getOutputSize(username.length())];
			int ctLength = cp.update(username.getBytes(), 0, username.length(), cipherUsername);
			cp.doFinal(cipherUsername, ctLength);

			cp.init(Cipher.ENCRYPT_MODE, specKey);
			byte[] cipherPassword = new byte[cp.getOutputSize(password.length())];
			ctLength = cp.update(password.getBytes(), 0, password.length(), cipherPassword);
			cp.doFinal(cipherPassword, ctLength);

			return new String(cipherUsername) + new String(cipherPassword);
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException | InvalidKeyException |
				ShortBufferException | IllegalBlockSizeException |
				BadPaddingException e) {
			e.printStackTrace();
		}

		// Shouldn't be reachable.
		return null;
	}
}
