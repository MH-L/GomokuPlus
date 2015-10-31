package Model;

public class ServerGame {
	private static final int SENTE = 1;
	private static final int GOTE = 2;
	private ServerBoard board;
	private int activePlayer;

	public ServerGame() {
		board = new ServerBoard();
		activePlayer = SENTE;
	}

	public void processRequest(String request) {
		if (request.startsWith("Move")) {
			// check for a valid move.
		} else if (request.startsWith("Quit")) {
			// check for player quit.
		} else if (request.startsWith("MSG")) {
			// process message (to be completed)
		} else if (request.startsWith("Surrender")) {
			// the player wants to give up.
		} else if (request.startsWith("Withdraw")) {
			// the player wants to try to withdraw.
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
			if (grid[xcoord][ycoord] != EMPTY_SPOT) {
				throw new InvalidMoveException(MOVE_SQUARE_OCCUPIED);
			}
			grid[xcoord][ycoord] = activePlayer == 1 ? SENTE_STONE : GOTE_STONE;
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
		return ServerConstants.WITHDRAW_DECLINED;
	}

	private int processSurrender() {
		return ServerConstants.REQUEST_OK;
	}

	private int processMove() {
		// TODO need to parse the string passed in.
		board.makeMove(1, 1);
		return ServerConstants.MOVE_OUT_BOUND;
	}
}
