package application;

import Model.ServerConstants;

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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import Model.Board;
import Model.Coordinate;
import Model.NetworkBoard;

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
	private static final int MESSAGE_INTERVAL = 500;
	private boolean peerConnected = true;
	private boolean gameStarted = false;
	private boolean messageReceived = false;
	private ArrayList<String> messageQueue = new ArrayList<String>();
	private String lastRequest = null;
	private Object booleanLock = new Object();
	private int turn;
	/**
	 * Status bar for game status. Indicating whether the game is started,
	 * paused or something else.
	 */
	private JLabel statusBar;
	private JLabel infoBar;
	private JLabel actionBar;
	/**
	 * Player is only allowed to propose withdraw once in each round.
	 */
	private boolean withdrawProposed = false;

	public NetworkGame() throws InterruptedException {
		super();
		historyPanel.removeAll();
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
		statusBar = new JLabel("Peer Not Connected");
		statusBar.setFont(smallGameFont);
		historyPanel.add(statusBar);
//		historyPanel.add(new JSeparator());
		infoBar = new JLabel("Turn Undetermined");
		infoBar.setFont(smallGameFont);
		actionBar = new JLabel("");
		actionBar.setFont(smallGameFont);
		historyPanel.add(infoBar);
		historyPanel.add(actionBar);
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
		initialSetUp2();
		addCellsToBoard();
		Thread coordinator = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			synchronized public void run() {
				System.out.println("Coordinator thread from game client up and running.");
				Thread socketListener = new Thread(new Runnable() {
					@Override
					synchronized public void run() {
						System.out.println("Socket listener up and running.");
						while (true) {
							try {
								String line = serverReader.readLine();
								if (line != "" && line != null) {
									System.out.println("Received reply from the server.");
									synchronized(booleanLock) {
										messageReceived = true;
									}
									synchronized(messageQueue) {
										messageQueue.add(line);
									}
								} else if (line == null) {
									JOptionPane.showMessageDialog(mainFrame, "You are disconnected from the game server."
											+ "\nPlease log on once again.", "Game Info -- Disconnected",
											JOptionPane.INFORMATION_MESSAGE);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
				Thread gameThread = new Thread(new Runnable() {
					@Override
					public void run() {
						System.err.println("Sending online request.");
						serverWriter.println("Online");
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								serverWriter.println(ServerConstants.STR_AVAILABLE);
							}
						}, MESSAGE_INTERVAL, MESSAGE_INTERVAL);
					}
				});
				socketListener.start();
				gameThread.start();
				while (true) {
					if (messageReceived) {
						gameThread.suspend();
						// do something here.
						try {
							handleServerMessage();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(mainFrame, "Internal Error!");
						}
						gameThread.resume();
						System.out.println("Game thread resumed");
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
	}

	public static void handleConnectionFailure() {
		JOptionPane.showMessageDialog(mainFrame, "Connection failed. Return to main page.");
	}

	@Override
	protected void initialSetUp() {

	}

	protected void initialSetUp2() {
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
		btnTryWithdraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (withdrawProposed) {
					JOptionPane.showMessageDialog(mainFrame, "You cannot propose withdrawal"
							+ " more than one\ntime in a round.", "Withdrawal Proposed Notice",
							JOptionPane.WARNING_MESSAGE);
					return;
				} else {
					withdrawProposed = true;
					serverWriter.println(ServerConstants.STR_WITHDRAW_REQUEST);
				}
			}
		});
		btnGiveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverWriter.println(ServerConstants.STR_GIVEUP_REQUEST);
			}
		});
		btnProposeTie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverWriter.println(ServerConstants.STR_TIE_REQUEST);
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
				square.addActionListener(new SquareActionListener(j, i));
				boardPanel.add(square);
				board.setSquare(j, i, square);
			}
		}
	}

	synchronized public void handleServerMessage() throws IOException {
		synchronized(messageQueue) {
			while (!messageQueue.isEmpty()) {
				String message = messageQueue.get(0);
				if (message.startsWith(String.valueOf(ServerConstants.INT_REQUEST_OK) + ",")) {
					// Probably too general; leave it for now.
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_SENTE) + ",")) {
					turn = Game.TURN_SENTE;
					actionBar.setText("Your Turn");
					infoBar.setText("You're First.");
					System.out.println("I am sente!");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_GOTE) + ",")) {
					turn = Game.TURN_GOTE;
					actionBar.setText("Opponent's Turn");
					infoBar.setText("You're Second");
					System.out.println("I am gote!");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_PEER_CONNECTED) + ",")) {
					statusBar.setText("Peer Connected");
					peerConnected = true;
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_GAME_START_APPORVED) + ",")) {
					statusBar.setText("Game Started");
					gameStarted = true;
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_DEFEAT) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "Your opponent wins. Good luck next time!",
							"Game Over -- You Lose", JOptionPane.INFORMATION_MESSAGE);
					statusBar.setText("You Lose");
					mainSocket.close();
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_VICTORY) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "Congratulations! You win!",
							"Game Over -- You Win", JOptionPane.INFORMATION_MESSAGE);
					statusBar.setText("You Win");
					mainSocket.close();
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_MOVE_SQUARE_OCCUPIED) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "The square is occupied. Please check"
							+ " your move.", "Re-move", JOptionPane.INFORMATION_MESSAGE);
					actionBar.setText("Turn Invalid --\nPlease Re-move");
					dirtyBit = false;
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_MOVE_OUT_BOUND) + ",")) {
					// Normally this should not happen.
					// If this happens, then there must be something wrong with game implementation.
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_NOT_YOUR_TURN) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "It is not your turn yet!", "Warning",
							JOptionPane.WARNING_MESSAGE);
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_MESSAGE) + ",")) {
					int confirmation = JOptionPane.showConfirmDialog(mainFrame,
							"Other player wants to withdraw."
							+ " Click\n\"yes\" to approve, \"no\" to decline.",
							"Withdraw Proposal", JOptionPane.YES_NO_OPTION);

					if (confirmation == JOptionPane.YES_OPTION) {
						serverWriter.println(ServerConstants.STR_WITHDRAW_APPROVED);
					} else {
						serverWriter.println(ServerConstants.STR_WITHDRAW_DECLINED);
					}
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_PEER_DISCONNECTED) + ",")) {
					System.out.println("Aaaaaa your peer doesn't like you!");
					statusBar.setText("Peer Quitted");
					int choice = JOptionPane.showConfirmDialog(mainFrame,
							"Your opponent quitted the game. Do you want to \n stay in the game?",
							"Opponent Quit", JOptionPane.INFORMATION_MESSAGE);
					if (choice == JOptionPane.YES_OPTION) {

					} else {

					}
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_YOUR_MOVE) + ",")) {
					String[] coords = message.split(",");
					int xcoord = Integer.parseInt(coords[1]);
					int ycoord = Integer.parseInt(coords[2]);
					dirtyBit = true;
					board.setSquareByTurn(ycoord, xcoord, turn);
					actionBar.setText("Opponent's Turn");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_OPPONENT_MOVE) + ",")) {
					String[] coords = message.split(",");
					int xcoord = Integer.parseInt(coords[1]);
					int ycoord = Integer.parseInt(coords[2]);
					dirtyBit = false;
					int otherTurn = (turn == Game.TURN_SENTE) ? Game.TURN_GOTE : Game.TURN_SENTE;
					board.setSquareByTurn(ycoord, xcoord, otherTurn);
					withdrawProposed = false;
					actionBar.setText("Your Turn");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_APPROVED) + ",")) {
					if (withdrawProposed) {
						JOptionPane.showMessageDialog(mainFrame, "Your withdrawal"
								+ " has been approved by your opponent!",
								"Congratulations!", JOptionPane.INFORMATION_MESSAGE);
					}
					String[] coordinates = message.split(",");
					if (coordinates.length == 5) {
						int firstX = Integer.parseInt(coordinates[1]);
						int firstY = Integer.parseInt(coordinates[2]);
						int secondX = Integer.parseInt(coordinates[3]);
						int secondY = Integer.parseInt(coordinates[4]);
						board.resetSquare(firstX, firstY);
						board.resetSquare(secondX, secondY);
					} else {
						int firstX = Integer.parseInt(coordinates[1]);
						int firstY = Integer.parseInt(coordinates[2]);
						board.resetSquare(firstX, firstY);
					}
					dirtyBit = false;
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_DECLINED) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "Unfortunately, your opponent declined"
							+ " your withdrawal request.", "Withdrawal Declined",
							JOptionPane.INFORMATION_MESSAGE);
					infoBar.setText("Withdraw Declined");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_TIE_DECLINED) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "Your opponent has declined your tie proposal."
							+ "\nGood luck next time!", "Tie Proposal Declined",
							JOptionPane.INFORMATION_MESSAGE);
					infoBar.setText("Tie Declined");
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_TIE_PROPOSED) + ",")) {
					int response = JOptionPane.showConfirmDialog(mainFrame,
									"Your opponent wants to tie the game."
									+ " Do you agree?", "Tie Proposal", JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.YES_OPTION) {
						serverWriter.println(ServerConstants.STR_TIE_APPROVED);
					} else {
						serverWriter.println(ServerConstants.STR_TIE_DECLINED);
					}
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_TIE) + ",")) {
					JOptionPane.showMessageDialog(mainFrame, "Tie! Game over!", "Game Over -- Tie",
							JOptionPane.INFORMATION_MESSAGE);
					statusBar.setText("Tie");
					mainSocket.close();
				} else if (message.startsWith(String.valueOf(ServerConstants.INT_WITHDRAW_FAILED))) {
					JOptionPane.showMessageDialog(mainFrame, "You have nothing to withdraw "
							+ "or you cannot\nwithdraw twice.", "Withdraw Failed",
							JOptionPane.WARNING_MESSAGE);
					infoBar.setText("Withdraw Failed");
				}
				messageQueue.remove(0);
			}
		}
		synchronized(booleanLock) {
			messageReceived = false;
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
			if (!gameStarted) {
				Game.warnGameFrozen();
				return;
			}
			if (dirtyBit == true) {
				multiClickWarning();
				return;
			}

			serverWriter.println(String.format("Move,%d,%d", xcoord, ycoord));
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
