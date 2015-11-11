package auth;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

	public static void authenticate(Socket clientSocket) {

	}
}
