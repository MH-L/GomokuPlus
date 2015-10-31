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
		private static final int width = 15;
		private static final int height = 15;
		private static int[][] grid;

		private ServerBoard() {
			grid = new int[height][width];
		}
	}
}
