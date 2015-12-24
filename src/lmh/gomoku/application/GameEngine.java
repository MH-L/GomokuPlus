package lmh.gomoku.application;

import renju.com.lmh.application.AI;
import renju.com.lmh.application.Game.Difficulty;
import renju.com.lmh.exception.InvalidIndexException;
import renju.com.lmh.model.Board;
import renju.com.lmh.model.BoardLocation;

public class GameEngine extends AI {
	private int turn;

	public GameEngine(Difficulty diff, Board board, boolean isFirst) {
		super(diff, board, isFirst);
		this.turn = isFirst ? Game.TURN_SENTE : Game.TURN_GOTE;
	}

	public boolean updateBoardForAnalysis(BoardLocation senteLastMove, boolean isForSelf)
			throws InvalidIndexException {
		boolean isFirst = isForSelf ? (turn == Game.TURN_SENTE)
				: (turn == Game.TURN_GOTE);
		return this.getSolver().getBoard()
				.updateBoard(new BoardLocation(senteLastMove.getYPos(), senteLastMove.getXPos()), isFirst);
	}

	public void endGameCleanup() {
		this.getSolver().getBoard().reset();
	}
}
