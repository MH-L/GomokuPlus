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
	private static final int RESULT_SENTE = 3;
	private static final int RESULT_GOTE = 2;
	private static final int RESULT_TIE = 4;
	private static final int RESULT_UNDECIDED = 1;
	private static final int NUM_STONE_TO_WIN = 5;
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
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					grid[i][j] = EMPTY_SPOT;
				}
			}
		}

		synchronized private void makeMove(int xcoord, int ycoord) throws InvalidMoveException {
			if (xcoord > width || ycoord >= height || xcoord < 0 || ycoord < 0) {
				throw new InvalidMoveException(MOVE_OUT_BOUND);
			}
			if (grid[ycoord][xcoord] != EMPTY_SPOT) {
				throw new InvalidMoveException(MOVE_SQUARE_OCCUPIED);
			}
			grid[ycoord][xcoord] = activePlayer == 1 ? SENTE_STONE : GOTE_STONE;
		}

		private int checkDiagWinning() {
			int i = 0;
			int j = 0;
			int iStartIndex = 0;
			int jStartIndex = 0;
			while (i + j < width + height - 1) {
				int counter = 0;
				int prev = EMPTY_SPOT;
				while (j > -1 && i < height) {
					if (grid[i][j] != EMPTY_SPOT) {
						if (grid[i][j] == prev) {
							counter ++;
						} else {
							counter = 1;
						}
					} else
						counter = 0;
					if (counter == NUM_STONE_TO_WIN) {
						if (grid[i][j] == SENTE_STONE) {
							return RESULT_SENTE;
						} else {
							return RESULT_GOTE;
						}
					}
					prev = grid[i][j];
					i++;
					j--;
				}
				if (jStartIndex >= width - 1) {
					iStartIndex ++;
				} else {
					jStartIndex ++;
				}
				i = iStartIndex;
				j = jStartIndex;
			}

			j = width - 1;
			i = 0;
			iStartIndex = 0;
			jStartIndex = width - 1;
			while (i - j < width) {
				int counter = 0;
				int prev = EMPTY_SPOT;
				while (j < width && i < height) {
					if (grid[i][j] != EMPTY_SPOT) {
						if (grid[i][j] == prev) {
							counter ++;
						} else {
							counter = 1;
						}
					} else {
						counter = 0;
					}
					if (counter == NUM_STONE_TO_WIN) {
						if (grid[i][j] == SENTE_STONE) {
							return RESULT_SENTE;
						} else {
							return RESULT_GOTE;
						}
					}
					prev = grid[i][j];
					i++;
					j++;
				}
				if (jStartIndex > 0) {
					jStartIndex --;
				} else {
					iStartIndex ++;
				}
				i = iStartIndex;
				j = jStartIndex;
			}
			return RESULT_UNDECIDED;
		}

		private int checkRowColWinning() {
			// Check for rows.
			for (int i = 0; i < height; i++) {
				int counter = 0;
				int prev = EMPTY_SPOT;
				for (int j = 0; j < width; j++) {
					if (grid[i][j] != EMPTY_SPOT) {
						if (grid[i][j] == prev) {
							counter ++;
						} else {
							counter = 1;
						}
					} else {
						counter = 0;
					}
					if (counter == NUM_STONE_TO_WIN) {
						if (grid[i][j] == SENTE_STONE) {
							return RESULT_SENTE;
						} else {
							return RESULT_GOTE;
						}
					}
					prev = grid[i][j];
				}
			}

			// Check for columns.
			for (int i = 0; i < width; i++) {
				int counter = 0;
				int prev = EMPTY_SPOT;
				for (int j = 0; j < height; j++) {
					if (grid[j][i] != EMPTY_SPOT) {
						if (grid[j][i] == prev) {
							counter ++;
						} else {
							counter = 1;
						}
					} else {
						counter = 0;
					}
					if (counter == NUM_STONE_TO_WIN) {
						if (grid[j][i] == SENTE_STONE) {
							return RESULT_SENTE;
						} else {
							return RESULT_GOTE;
						}
					}
					prev = grid[j][i];
				}
			}
			return RESULT_UNDECIDED;
		}

		private int gameOver() {
			if (boardFull()) {
				return RESULT_TIE;
			}
			int resultRowCol = checkRowColWinning();
			if (resultRowCol != RESULT_UNDECIDED) {
				return resultRowCol;
			} else {
				return checkDiagWinning();
			}
		}

		private boolean boardFull() {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < height; j++) {
					if (grid[i][j] == EMPTY_SPOT) {
						return false;
					}
				}
			}
			return true;
		}
	}

	private static class InvalidMoveException extends Exception {
		private static final long serialVersionUID = 1L;
		private int errorReason;

		private InvalidMoveException(int errorReason) {
			this.errorReason = errorReason;
		}
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
			player2.notifyOpponentMove(xcoord, ycoord);
		} else {
			player1.notifyOpponentMove(xcoord, ycoord);
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

		private ServerPlayer(Socket playerSocket, final int turn) throws IOException {
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
										if (turn == SENTE) {
											ServerGame.this.player1Alive = false;
										} else {
											ServerGame.this.player2Alive = false;
										}
										sendQuitMessage();
										return;
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

		private void sendGameTieMessage() {
			serverOut.println(ServerConstants.INT_TIE + ",");
		}

		private void sendGameStartMessage() {
			serverOut.println(ServerConstants.INT_GAME_START_APPORVED + ",");
		}

		private void sendWithdrawMessage(int firstX, int firstY, int secondX, int secondY) {
			if (secondX == -1 && secondY == -1) {
				serverOut.println(ServerConstants.INT_WITHDRAW_APPROVED +
						String.format(",%d,%d", firstX, firstY));
			} else {
				serverOut.println(ServerConstants.INT_WITHDRAW_APPROVED +
						String.format(",%d,%d,%d,%d", firstX, firstY, secondX, secondY));
			}
		}

		private void sendTurnMessage() {
			if (turn == SENTE) {
				serverOut.println(ServerConstants.INT_SENTE + ",");
			} else {
				serverOut.println(ServerConstants.INT_GOTE + ",");
			}
		}

		private void sendWithdrawDeclinedMsg() {
			serverOut.println(ServerConstants.INT_WITHDRAW_DECLINED + ",");
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
							// TODO turn message not correct.
							sendPeerConnectedMessage();
							promptOtherPlayerPeerConnected(turn);
						}
					} else {
						player2Alive = true;
						if (player1Alive) {
							sendPeerConnectedMessage();
							promptOtherPlayerPeerConnected(turn);
						}
					}
					sendTurnMessage();
				} else if (req.startsWith(ServerConstants.STR_MOVE_REQUEST)) {
					if (player1Alive && player2Alive) {
						if (turn == activePlayer) {
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
								synchronized(requestQueue) {
									requestQueue.remove(0);
								}
								continue;
							}
							if (turn == SENTE) {
								player1LastMove = new Move(xcoord, ycoord);
							} else {
								player2LastMove = new Move(xcoord, ycoord);
							}
							ServerGame.this.updateActivePlayer();
							ServerGame.this.promptOtherPlayerOppnentMove(turn, xcoord, ycoord);
							notifySelfMove(xcoord, ycoord);
							int gameResult = board.gameOver();
							if (gameResult != RESULT_UNDECIDED) {
								if (gameResult == RESULT_SENTE) {
									if (turn == SENTE) {
										sendWinMessage();
										ServerGame.this.promptOtherPlayerForLost(turn);
									} else {
										sendLostMessage();
										ServerGame.this.promptOtherPlayerForVictory(turn);
									}
								} else if (gameResult == RESULT_GOTE) {
									if (turn == GOTE) {
										sendWinMessage();
										ServerGame.this.promptOtherPlayerForLost(turn);
									} else {
										sendLostMessage();
										ServerGame.this.promptOtherPlayerForVictory(turn);
									}
								} else {
									serverOut.println(ServerConstants.INT_TIE + ",");
									ServerGame.this.promptOtherPlayerForTie(turn);
								}
							}
						} else {
							serverOut.println(ServerConstants.INT_NOT_YOUR_TURN + ",");
						}
					} else {
						sendQuitMessage();
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
					// TODO message services have not been implemented yet.
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
					sendGameTieMessage();
					promptOtherPlayerTieMessage(turn);
				} else if (req.startsWith(ServerConstants.STR_TIE_DECLINED)) {
					serverOut.println(ServerConstants.INT_TIE_DECLINED + ",");
				} else if (req.startsWith(ServerConstants.STR_WITHDRAW_APPROVED)) {
					// TODO the withdraw logic is not flawless.
					// maybe just tell the client which moves to withdraw?
					int firstX = -1, firstY = -1, secondX = -1, secondY = -1;
					if (turn == SENTE && player2LastMove != null) {
						firstX = player2LastMove.x;
						firstY = player2LastMove.y;
						ServerGame.this.board.grid[firstY][firstX] = ServerBoard.EMPTY_SPOT;
						updateActivePlayer();
						if (activePlayer == GOTE) {
							secondX = player1LastMove.x;
							secondY = player2LastMove.y;
							ServerGame.this.board.grid[secondY][secondX] = ServerBoard.EMPTY_SPOT;
						}
						sendWithdrawMessage(firstX, firstY, secondX, secondY);
						ServerGame.this.promptOtherPlayerWithdraw(turn, firstX, firstY, secondX, secondY);
					} else if (player1LastMove != null) {
						firstX = player1LastMove.x;
						firstY = player1LastMove.y;
						ServerGame.this.board.grid[firstY][firstX] = ServerBoard.EMPTY_SPOT;
						updateActivePlayer();
						if (activePlayer == SENTE) {
							secondX = player2LastMove.x;
							secondY = player2LastMove.y;
							ServerGame.this.board.grid[secondY][secondX] = ServerBoard.EMPTY_SPOT;
						}
						sendWithdrawMessage(firstX, firstY, secondX, secondY);
						ServerGame.this.promptOtherPlayerWithdraw(turn, firstX, firstY, secondX, secondY);
					} else {
						serverOut.println(ServerConstants.INT_WITHDRAW_FAILED + ",");
					}
				} else if (req.startsWith(ServerConstants.STR_WITHDRAW_DECLINED)) {
					ServerGame.this.promptOtherPlayerWithdrawDeclined(turn);
				} else if (req.startsWith(ServerConstants.STR_WITHDRAW_REQUEST)) {
					if ((turn == SENTE && player1LastMove == null)
							|| (turn == GOTE && player2LastMove == null)) {
						serverOut.println(ServerConstants.INT_WITHDRAW_FAILED + ",");
					}
					ServerGame.this.promptOtherPlayerForWithdraw(turn);
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

		private void notifyOpponentMove(int xcoord, int ycoord) {
			serverOut.println(String.format("%d,%d,%d", ServerConstants.INT_OPPONENT_MOVE,
					xcoord, ycoord));
		}

		private void notifySelfMove(int xcoord, int ycoord) {
			serverOut.println(String.format("%d,%d,%d",
					ServerConstants.INT_YOUR_MOVE, xcoord, ycoord));
		}

		private void sendLostMessage() {
			serverOut.println(ServerConstants.INT_DEFEAT + ",");
		}

		private void sendWinMessage() {
			serverOut.println(ServerConstants.INT_VICTORY + ",");
		}

		public void sendWithdrawRequestMsg() {
			serverOut.println(ServerConstants.INT_WITHDRAW_MESSAGE + ",");
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

	private void promptOtherPlayerTieMessage(int prompted) {
		if (prompted == SENTE) {
			player2.sendGameTieMessage();
		} else {
			player1.sendGameTieMessage();
		}
	}

	private void promptOtherPlayerWithdraw(int turn, int firstX,
			int firstY, int secondX, int secondY) {
		if (turn == SENTE) {
			player2.sendWithdrawMessage(firstX, firstY, secondX, secondY);
		} else {
			player1.sendWithdrawMessage(firstX, firstY, secondX, secondY);
		}
	}

	private void promptOtherPlayerOppnentMove(int turn, int xcoord, int ycoord) {
		if (turn == SENTE) {
			player2.notifyOpponentMove(xcoord, ycoord);
		} else {
			player1.notifyOpponentMove(xcoord, ycoord);
		}
	}

	private void promptOtherPlayerForLost(int turn) {
		if (turn == SENTE) {
			player2.sendLostMessage();
		} else {
			player1.sendLostMessage();
		}
	}

	private void promptOtherPlayerForVictory(int turn) {
		if (turn == SENTE) {
			player2.sendWinMessage();
		} else {
			player1.sendWinMessage();
		}
	}

	private void promptOtherPlayerForWithdraw(int turn) {
		if (turn == SENTE) {
			player2.sendWithdrawRequestMsg();
		} else {
			player1.sendWithdrawRequestMsg();
		}
	}

	private void promptOtherPlayerWithdrawDeclined(int turn) {
		if (turn == SENTE) {
			player2.sendWithdrawDeclinedMsg();
		} else {
			player1.sendWithdrawDeclinedMsg();
		}
	}
}
