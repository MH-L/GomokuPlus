package Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
	private static final int PORT = 1031;
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(PORT);
		Socket clientSocket = ss.accept();
	}
}
