package Model;

public class ServerGame {
	private static ServerBoard board;

	public ServerGame() {
		board = new ServerBoard();
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

	private static class ServerBoard {
		private static final int MOVE_OUT_BOUND = 1;
		private static final int MOVE_SQUARE_OCCUPIED = 2;
		private static final int width = 15;
		private static final int height = 15;
		private static int[][] grid;

		private ServerBoard() {
			grid = new int[height][width];
		}

		private void makeMove(int xcoord, int ycoord) throws InvalidMoveException {

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
		return ServerConstants.MOVE_OUT_BOUND;
	}
}
