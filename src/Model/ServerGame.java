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
	private boolean gameStarted = false;
	private boolean player1Started = false;
	private boolean player2Started = false;
	private ArrayList<String> requestQueue = new ArrayList<String>();

	public ServerGame(Socket player1Socket, Socket player2Socket) throws IOException {
		board = new ServerBoard();
		activePlayer = SENTE;
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

	private class ServerPlayer {
		private BufferedReader playerIn;
		private PrintWriter serverOut;
		private int turn;
		private ServerPlayer(Socket playerSocket, int turn) throws IOException {
			playerIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
			serverOut = new PrintWriter(playerSocket.getOutputStream(), true);
			this.turn = turn;
		}

		private void sendQuitMessage() {
			serverOut.println(ServerConstants.INT_PEER_DISCONNECTED + ",");
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
			serverOut.println(String.format("%d,%d,%d", ServerConstants.INT_OTHER_PLAYER_MOVE,
					xcoord, ycoord));
		}
	}

	private void updateActivePlayer() {
		activePlayer = activePlayer == GOTE ? SENTE : GOTE;
	}
}
