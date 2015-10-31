package Model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import application.NetworkGame;

public class NetworkBoard extends Board {
	private Socket mainSocket;
	private static final String HOST = "104.236.97.57";
	private static final int PORT = 1031;

	public NetworkBoard(JPanel boardPanel) {
		super(boardPanel);
		try {
			mainSocket = new Socket(HOST, PORT);
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
				square.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

					}
				});
			}
		}
	}


}
