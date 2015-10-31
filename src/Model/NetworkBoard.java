package Model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import application.NetworkGame;

public final class NetworkBoard extends Board {
	private Socket mainSocket;
	private static final String HOST = "104.236.97.57";
	private static final int PORT = 1031;
	private static BufferedReader serverReader;
	private static PrintWriter serverWriter;

	public NetworkBoard(JPanel boardPanel) {
		super(boardPanel);
		try {
			mainSocket = new Socket(HOST, PORT);
			serverReader = new BufferedReader(new InputStreamReader(
					mainSocket.getInputStream()));
			serverWriter = new PrintWriter(mainSocket.getOutputStream(), true);
		} catch (IOException e) {
			NetworkGame.handleConnectionFailure();
		}
	}

	@Override
	public void addCellsToBoard(JPanel boardPanel) {
		boardPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		for (int i = 0; i < Board.height; i++) {
			for (int j = 0; j < Board.width; j++) {
				Coordinate square = new Coordinate(i, j);
				square.setBackground(Color.YELLOW);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new SquareActionListener(i, j));
			}
		}
	}

	private static class SquareActionListener implements ActionListener {
		private static int xcoord;
		private static int ycoord;

		public SquareActionListener(int x, int y) {
			xcoord = x;
			ycoord = y;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			serverWriter.println(String.format("Move, %d, %d", xcoord, ycoord));
		}

	}

}
