package lmh.gomoku.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Authentication service for the gomoku game.
 * Date: 2015/11/11
 * @author Minghao
 */
public class AuthService {
	public static final int PORT = 1993;

	public static void main(String[] args) {
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
	public static boolean verifyPass(String password) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,17}$";
		return password.matches(regex);
	}

	/**
	 * Checks if a username matches our standard. A username is valid if and only if
	 * it contains only upper/lowercase letters, numbers and underscores.
	 * @param username
	 * @return
	 */
	public static boolean verifyUsername(String username) {
		String regex = "^[a-zA-Z0-9]{4,16}$";
		return username.matches(regex);
	}
}
