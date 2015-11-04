package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerGame {
	private static final int SENTE = 1;
	private static final int GOTE = 2;
	private ServerBoard board;
	private int activePlayer;
	private ServerPlayer player1;
	private ServerPlayer player2;
	volatile private boolean gameStarted = false;
	volatile private boolean player1Alive = false;
	volatile private boolean player2Alive = false;
	private Move player1LastMove = null;
	private Move player2LastMove = null;
	volatile private boolean giveUpReceived = false;

	public ServerGame(Socket player1Socket, Socket player2Socket) throws IOException, InterruptedException {
		board = new ServerBoard();
		activePlayer = SENTE;
		System.out.println("Game shall be started.");
		player1 = new ServerPlayer(player1Socket, SENTE);
		player2 = new ServerPlayer(player2Socket, GOTE);
	}

	public int processRequest(String request, int turn) {
		if (request.startsWith("Move")) {
			String[] coords = request.split(",");
			int xcoord = Integer.parseInt(coords[1]);
			int ycoord = Integer.parseInt(coords[2]);
			return processMove(xcoord, ycoord);
		} else if (request.startsWith("Quit")) {
			if (turn == SENTE) {
				player1 = null;
				player2.sendQuitMessage();
			} else {
				player2 = null;
				player1.sendQuitMessage();
			}
			return ServerConstants.INT_REQUEST_OK;
		} else if (request.startsWith("MSG")) {
			// process message (to be completed)
		} else if (request.startsWith("Surrender")) {
			// the player wants to give up.
		} else if (request.startsWith("Withdraw")) {
			// the player wants to try to withdraw.
		}
		return 1;
	}

	private void promptPlayerForVictory(int loserTurn) {
		if (loserTurn == SENTE) {
			player2.sendVictoryMessage();
		} else {
			player1.sendVictoryMessage();
		}
	}

	private void promptPlayerForQuit(int quitter) {
		if (quitter == SENTE) {
			player2.sendQuitMessage();
		} else {
			player1.sendQuitMessage();
		}
	}

	private class ServerBoard {
		private static final int MOVE_OUT_BOUND = 1;
		private static final int MOVE_SQUARE_OCCUPIED = 2;
		private static final int EMPTY_SPOT = 0;
		private static final int SENTE_STONE = 1;
		private static final int GOTE_STONE = 2;
		private static final int width = 15;
		private static final int height = 15;
		private int[][] grid;

		private ServerBoard() {
			grid = new int[height][width];
		}

		private void makeMove(int xcoord, int ycoord) throws InvalidMoveException {
			if (xcoord > width || ycoord >= height || xcoord < 0 || ycoord < 0) {
				throw new InvalidMoveException(MOVE_OUT_BOUND);
			}
			if (grid[ycoord][xcoord] != EMPTY_SPOT) {
				throw new InvalidMoveException(MOVE_SQUARE_OCCUPIED);
			}
			grid[ycoord][xcoord] = activePlayer == 1 ? SENTE_STONE : GOTE_STONE;
		}
	}

	private static class InvalidMoveException extends Exception {
		private static final long serialVersionUID = 1L;
		private int errorReason;

		private InvalidMoveException(int errorReason) {
			this.errorReason = errorReason;
		}
	}

	private int processWithdraw() {
		return ServerConstants.INT_WITHDRAW_DECLINED;
	}

	private int processSurrender() {
		return ServerConstants.INT_REQUEST_OK;
	}

	private int processMessage() {
		return ServerConstants.INT_REQUEST_OK;
	}

	private int processPlayerQuit() {
		return ServerConstants.INT_REQUEST_OK;
	}

	private int processMove(int xcoord, int ycoord) {
		// TODO need to parse the string passed in.
		try {
			board.makeMove(xcoord, ycoord);
		} catch (InvalidMoveException e) {
			if (e.errorReason == ServerBoard.MOVE_OUT_BOUND) {
				return ServerConstants.INT_MOVE_OUT_BOUND;
			} else {
				return ServerConstants.INT_MOVE_SQUARE_OCCUPIED;
			}
		}
		if (activePlayer == SENTE) {
			player2.notifyMove(xcoord, ycoord);
		} else {
			player1.notifyMove(xcoord, ycoord);
		}
		updateActivePlayer();
		return ServerConstants.INT_REQUEST_OK;
	}

	private class Move {
		private int x;
		private int y;

		private Move(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private class ServerPlayer {
		private BufferedReader playerIn;
		private PrintWriter serverOut;
		private int turn;
		private Thread coordinator;
		private Thread socketListener;
		private Thread gameThread;
		private ArrayList<String> requestQueue = new ArrayList<String>();

		private ServerPlayer(Socket playerSocket, int turn) throws IOException {
			playerIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
			serverOut = new PrintWriter(playerSocket.getOutputStream(), true);
			this.turn = turn;
			coordinator = new Thread(new Runnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					System.out.println("Coordinator on server is running.");
					socketListener = new Thread(new Runnable() {
						@Override
						public void run() {
							System.out.println("Socket listener Checking request Queue.");
							while (true) {
								try {
									String inline = playerIn.readLine();
									if (inline == null) {
										// TODO indicate the game is over.
										break;
									}
									synchronized(requestQueue) {
										requestQueue.add(inline);
									}
								} catch (IOException e) {
									// TODO Game is finished.
								}
							}
						}
					});
					gameThread = new Thread(new Runnable() {
						@Override
						public void run() {
							System.out.println("Game thread dealing with game logics.");
						}
					});
					socketListener.start();
					gameThread.start();
					while (true) {
						if (!requestQueue.isEmpty()) {
							System.out.println("Received message from game client.");
							gameThread.suspend();
							handlePlayerRequests();
							gameThread.resume();
							System.out.println("Game thread is resumed!");
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							System.out.println("Oops, interrupted!");
						}
					}
				}
			});
			coordinator.start();
		}

		private void sendPeerConnectedMessage() {
			serverOut.println(ServerConstants.INT_PEER_CONNECTED + ",");
		}

		private void sendQuitMessage() {
			serverOut.println(ServerConstants.INT_PEER_DISCONNECTED + ",");
		}

		private void sendVictoryMessage() {
			serverOut.println(ServerConstants.INT_VICTORY + ",");
		}

		private void sendTieRequestMessage() {
			serverOut.println(ServerConstants.INT_TIE_PROPOSED + ",");
		}

		private void sendGameStartMessage() {
			serverOut.println(ServerConstants.INT_GAME_START_APPORVED + ",");
		}

		synchronized private void handlePlayerRequests() {
			while (!requestQueue.isEmpty()) {
				System.out.println("Request queue is not empty... dealing with requests sent from user.");
				String req = "";
				synchronized(requestQueue) {
					req = requestQueue.get(0);
				}
				if (req.startsWith(ServerConstants.STR_ONLINE)) {
					System.out.println("Should have received this two times here.");
					if (turn == SENTE) {
						player1Alive = true;
						if (player2Alive) {
							sendPeerConnectedMessage();
						}
					} else {
						player2Alive = true;
						if (player1Alive) {
							sendPeerConnectedMessage();
						}
					}
				} else if (req.startsWith(ServerConstants.STR_MOVE_REQUEST)) {
					String[] coordinates = req.split(",");
					int xcoord = Integer.parseInt(coordinates[1]);
					int ycoord = Integer.parseInt(coordinates[2]);
					try {
						board.makeMove(xcoord, ycoord);
					} catch (InvalidMoveException e) {
						if (e.errorReason == ServerBoard.MOVE_OUT_BOUND) {
							serverOut.println(ServerConstants.INT_MOVE_OUT_BOUND + ",");
						} else {
							serverOut.println(ServerConstants.INT_MOVE_SQUARE_OCCUPIED + ",");
						}
					}
				} else if (req.startsWith(ServerConstants.STR_GIVEUP_REQUEST)) {
					if (!giveUpReceived) {
						serverOut.println(ServerConstants.INT_DEFEAT + ",");
						ServerGame.this.promptPlayerForVictory(turn);
					}
				} else if (req.startsWith(ServerConstants.STR_QUIT)) {
					boolean isNecessary = false;
					if (turn == SENTE) {
						isNecessary = player2Alive;
					} else {
						isNecessary = player1Alive;
					}
					if (isNecessary) {
						ServerGame.this.promptPlayerForQuit(turn);
					}
				} else if (req.startsWith(ServerConstants.STR_MESSAGE_REQUEST)) {

				} else if (req.startsWith(ServerConstants.STR_TIE_REQUEST)) {
					ServerGame.this.promptOtherPlayerForTie(turn);
				} else if (req.startsWith(ServerConstants.STR_REQUEST_GAME_START)) {
					if (!gameStarted) {
						if (turn == SENTE) {
							if (player2Alive) {
								gameStarted = true;
								sendGameStartMessage();
								ServerGame.this.promptOtherPlayerForStart(turn);
							}
						} else {
							if (player1Alive) {
								gameStarted = true;
								sendGameStartMessage();
								ServerGame.this.promptOtherPlayerForStart(turn);
							}
						}
					}
				} else if (req.startsWith(ServerConstants.STR_TIE_APPROVED)) {

				} else if (req.startsWith(ServerConstants.STR_TIE_DECLINED)) {

				} else if (req.startsWith(ServerConstants.STR_WITHDRAW_APPROVED)) {

				} else if (req.startsWith(ServerConstants.STR_WITHDRAW_DECLINED)) {

				}
				synchronized(requestQueue) {
					requestQueue.remove(0);
				}
			}
		}

		private void play() {
			while (true) {
				try {
					String playerRequest = playerIn.readLine();
					int response = ServerGame.this.processRequest(playerRequest, turn);
					serverOut.println(response + ",");
				} catch (IOException e) {
					break;
				}

			}
		}

		private void notifyMove(int xcoord, int ycoord) {
			serverOut.println(String.format("%d,%d,%d", ServerConstants.INT_OPPONENT_MOVE,
					xcoord, ycoord));
		}
	}

	private void updateActivePlayer() {
		activePlayer = activePlayer == GOTE ? SENTE : GOTE;
	}

	private void promptOtherPlayerForTie(int proposerTurn) {
		if (proposerTurn == SENTE) {
			player2.sendTieRequestMessage();
		} else {
			player1.sendTieRequestMessage();
		}
	}

	private void promptOtherPlayerForStart(int alreadyInformed) {
		if (alreadyInformed == SENTE) {
			player2.sendGameStartMessage();
		} else {
			player1.sendGameStartMessage();
		}
	}

	private void promptOtherPlayerPeerConnected(int alreadyConnected) {
		if (alreadyConnected == SENTE) {
			player2.sendPeerConnectedMessage();
		} else {
			player1.sendPeerConnectedMessage();
		}
	}
}
