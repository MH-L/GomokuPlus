package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Model.Board;
import Model.Coordinate;
import Model.NetworkBoard;
import Model.ServerConstants;

public class NetworkGame extends Game {
	private JButton btnProposeTie;
	private JButton btnTryWithdraw;
	private static NetworkBoard board;
	private static BufferedReader serverReader;
	private static PrintWriter serverWriter;
	private boolean dirtyBit = false;
	private Socket mainSocket;
	private static final String HOST = "104.236.97.57";
	private static final int PORT = 1031;
	private boolean peerConnected = false;
	private boolean gameStarted = false;
	private boolean messageReceived = false;
	private ArrayList<String> messageQueue = new ArrayList<String>();

	public NetworkGame() throws InterruptedException {
		super();
		btnProposeTie = Main.getPlainLookbtn("<html>Propose<br>Tie!</html>",
				"Open Sans", 28, Font.ITALIC, Color.GREEN);
		btnProposeTie.setMargin(new Insets(0, 0, 0, 0));
		buttonPanel.add(btnProposeTie);
		btnTryWithdraw = Main.getPlainLookbtn("<html>Try<br>Withdraw</html>",
				"Open Sans", 28, Font.PLAIN, Color.YELLOW);
		btnTryWithdraw.setMargin(new Insets(0, 0, 0, 0));
		buttonPanel.add(btnTryWithdraw);
		JLabel titleLabel = new JLabel("<html>Network Game<br></html>");
		titleLabel.setFont(Game.largeGameFont);
		titlePanel.add(titleLabel);
		board = new NetworkBoard(boardPanel);
		try {
			mainSocket = new Socket(HOST, PORT);
			System.out.println("Has successfully binded to that address and port.");
			serverReader = new BufferedReader(new InputStreamReader(
					mainSocket.getInputStream()));
			serverWriter = new PrintWriter(mainSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			NetworkGame.handleConnectionFailure();
		}
		addCellsToBoard();
		Thread coordinator = new Thread(new Runnable() {
			@Override
			synchronized public void run() {
				Thread socketListener = new Thread(new Runnable() {
					@Override
					synchronized public void run() {
						while (true) {
							try {
								String line = serverReader.readLine();
								if (line != "" && line != null) {
									messageReceived = true;
									messageQueue.add(line);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
				Thread gameThread = new Thread(new Runnable() {
					@Override
					synchronized public void run() {

					}
				});
				socketListener.start();
				gameThread.start();
				while (true) {
					if (messageReceived = true) {
						gameThread.interrupt();
						// do something here.
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		coordinator.start();
		coordinator.join();
	}

	public static void handleConnectionFailure() {
		JOptionPane.showMessageDialog(mainFrame, "Connection failed. Return to main page.");
	}

	@Override
	protected void initialSetUp() {
		System.out.println("This is called.");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (peerConnected) {
					serverWriter.println("Start");
				} else {
					JOptionPane.showMessageDialog(mainFrame,
							"The other player has not connected to the server."
							+ " Please wait.", "Warning: Peer not connected",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	public void addCellsToBoard() {
		System.out.println("Adding cells to the board.");
		boardPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		for (int i = 0; i < Board.height; i++) {
			for (int j = 0; j < Board.width; j++) {
				Coordinate square = new Coordinate(i, j);
				square.setBackground(Color.YELLOW);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new SquareActionListener(i, j));
				boardPanel.add(square);
				board.setSquare(j, i, square);
			}
		}
	}

	synchronized public void handleServerMessage() {
		while (!messageQueue.isEmpty()) {
			String message = messageQueue.get(0);
			if (message.startsWith(String.valueOf((ServerConstants.INT_REQUEST_OK)))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_SENTE))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_GOTE))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_DEFEAT))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_VICTORY))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_MOVE_SQUARE_OCCUPIED))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_MOVE_SQUARE_OCCUPIED))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_NOT_YOUR_TURN))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_MESSAGE))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_APPROVED))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_PEER_DISCONNECTED))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_DECLINED))) {

			} else if (message.startsWith(String.valueOf(ServerConstants.INT_OTHER_PLAYER_MOVE))) {
				String[] coords = message.split(",");
				int xcoord = Integer.parseInt(coords[1]);
				int ycoord = Integer.parseInt(coords[2]);

			}
			messageQueue.remove(0);
		}
	}

	private void setSquareOpponent(int x, int y) {

	}

	private class SquareActionListener implements ActionListener {
		private int xcoord;
		private int ycoord;

		public SquareActionListener(int x, int y) {
			xcoord = x;
			ycoord = y;
		}

		/**
		 * Anything that changes the dirty bit must be synchronized.
		 */
		@Override
		synchronized public void actionPerformed(ActionEvent e) {
			if (dirtyBit == true) {
				multiClickWarning();
				return;
			}

			dirtyBit = true;
			serverWriter.println(String.format("Move,%d,%d", xcoord, ycoord));
			try {
				String message = serverReader.readLine();
			} catch (IOException e1) {
				return;
			}
		}

	}

	private static void multiClickWarning() {
		JOptionPane.showMessageDialog(mainFrame, "Please do not click a square multiple times"
				+ " or click on multiple squares when making move.",
				"Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Synchronized since it changes the dirty bit and also
	 * receives responses from the game server.
	 */
	synchronized private void play() {
		while (true) {
			while (!dirtyBit) {
				// still have to wait until the server gives a response.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					continue;
				}
				try {
					String serverResponse = serverReader.readLine();
					if (serverResponse.startsWith(String.valueOf(ServerConstants.INT_REQUEST_OK))) {

					}
				} catch (IOException e) {
					return;
				}
			}
		}
	}
}
