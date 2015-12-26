package lmh.gomoku.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

import lmh.gomoku.auth.AuthConstants;
import lmh.gomoku.exception.RegistrationException;

public class AuthClient {
	private static final String AUTH_HOST = "104.236.97.57";
	private static final int AUTH_PORT = 1993;

	/**
	 * Send create account request with given username and password.
	 * NOTE: For all auth activities, the client initializes the conversation.
	 * Therefore no other threads are required.
	 * @param username
	 * @param password
	 * @param credential
	 * @throws RegistrationException
	 */
	public static void createAccount(String username, String password, String credential)
			throws RegistrationException {
		try {
			Socket sc = new Socket(AUTH_HOST, AUTH_PORT);
			String encryptedUName = new String
					(Base64.getEncoder().encode(username.getBytes()));
			String encryptedPassword = new String
					(Base64.getEncoder().encode(password.getBytes()));
			String encryptedCredential = new String
					(Base64.getEncoder().encode(credential.getBytes()));
			BufferedReader serverReader = new BufferedReader
					(new InputStreamReader(sc.getInputStream()));
			PrintWriter serverWriter = new PrintWriter(sc.getOutputStream(), true);

			serverWriter.println(String.format("%d,%s,%s,%s", AuthConstants.INT_CREATE_ACCOUNT,
					encryptedUName, encryptedPassword, encryptedCredential));

			String response = serverReader.readLine();
			String successHeader = AuthConstants.INT_ACTION_SUCCESS + ",";
			String failureHeader = AuthConstants.INT_GENERAL_ERROR + ",";

			if (response.startsWith(successHeader)) {

			} else if (response.startsWith(failureHeader)) {

			} else {
				throw new RegistrationException("Authentication server returns unknown response.");
			}
		} catch (UnknownHostException e) {
			throw new RegistrationException("Cannot connect to authentication server.");
		} catch (IOException e) {
			throw new RegistrationException("Authentication server down.");
		}

	}

	public static void changeUserName(String newUsername, String oldUsername, String password) {

	}

	public static void userLogin(String username, String password) {

	}

	public static void loginUsingToken(String token) {

	}
}
