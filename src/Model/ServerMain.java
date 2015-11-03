package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	private static final int PORT = 1031;
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("Game Server for GomokuPlus is running...");
		try {
			while (true) {
				Socket clientSocket1 = ss.accept();
				Socket clientSocket2 = ss.accept();
				try {
					ServerGame game = new ServerGame(clientSocket1, clientSocket2);
				} catch (IOException | InterruptedException e) {
					System.out.println("Cannot reliably bind on the socket or the game"
							+ " cannot be started. Exiting...");
					System.exit(1);
				}
			}
		} finally {
			ss.close();
		}
	}
}
