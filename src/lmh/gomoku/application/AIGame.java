package lmh.gomoku.application;

import javax.swing.JButton;
import javax.swing.JLabel;

import renju.com.lmh.application.Game.Difficulty;
import renju.com.lmh.exception.InvalidIndexException;
import renju.com.lmh.model.BoardLocation;
import lmh.gomoku.application.Game;
import lmh.gomoku.model.Board;

/**
 * A class for the kind of game which involves only two
 * computer players. They make turn one by one until the result of the game
 * is decided. This model is different from AIGame in Renju in the sense that
 * two computer players run on different children threads instead of both on the main
 * thread. This also facilitates caching of game trees (with two players running on main
 * thread, concurrent modification can be hard to deal with).
 * @author Minghao
 *
 */
public class AIGame extends Game {
	/**
	 * Animation interval in milliseconds.
	 * This is the interval between two AI moves.
	 */
	private static int animationInterval = 1000;
	/**
	 * Two AI threads, one thread for each computer player.
	 */
	private Thread senteThread;
	private Thread goteThread;
	/**
	 * Coordinator thread is the thread for monitoring game process
	 * and updating boards for each engine.
	 */
	private Thread coordinator;
	/**
	 * Two AI engines, senteEngine for the computer player who makes
	 * move first, and goteEngine for the computer player who makes move second.
	 */
	private GameEngine senteEngine;
	private GameEngine goteEngine;
	/**
	 * Indicating whose turn it is. True means SENTE is
	 * making its move, and false means GOTE is making move.
	 */
	private boolean activePlayer = true;
	/**
	 * Lock for active player boolean variable.
	 */
	private Object lock = new Object();
	/**
	 * Board locations that need to be updated onto analysis boards
	 * of each engine.
	 */
	private BoardLocation senteLastMove = getNullMove();
	private BoardLocation goteLastMove = getNullMove();

	public AIGame() {
		this(Difficulty.INTERMEDIATE, Difficulty.INTERMEDIATE);
	}

	public AIGame(Difficulty senteDiff, Difficulty goteDiff) {
		super();
		board.freeze();
		JLabel titleLabel = new JLabel("AI Game");
		titleLabel.setFont(smallGameFont);
		titlePanel.add(titleLabel);
		renju.com.lmh.model.Board senteAnalysisBoard = new renju.com.lmh.model.Board(Board.width);
		renju.com.lmh.model.Board goteAnalysisBoard = new renju.com.lmh.model.Board(Board.width);
		senteEngine = new GameEngine(senteDiff, senteAnalysisBoard, true);
		goteEngine = new GameEngine(goteDiff, goteAnalysisBoard, false);
		automaticStart();
	}

	private void automaticStart() {
		board.activate();
		senteThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (activePlayer && !board.isFrozen()) {
						// time to make move when active player is true
						try {
							BoardLocation aiMove = senteEngine.makeMove();
							synchronized(senteLastMove) {
								senteLastMove = aiMove;
							}
							AIGame.this.board.setSquareStoneByTurn(aiMove.getXPos(),
									aiMove.getYPos(), Game.TURN_SENTE);
							AIGame.this.board.setSquareIconByTurn(aiMove.getXPos(),
									aiMove.getYPos(), Game.TURN_SENTE);
						} catch (InvalidIndexException e) {
							// Normally we wouldn't encounter this. This only happens if
							// there are bugs in our AI code.
							e.printStackTrace();
						}
					} else if (board.isFrozen()) {
						return;
					}

					try {
						Thread.sleep(animationInterval);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		};

		goteThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!activePlayer && !board.isFrozen()) {
						// time to make move when active player is false
						try {
							BoardLocation aiMove = goteEngine.makeMove();
							synchronized(goteLastMove) {
								goteLastMove = aiMove;
							}
							AIGame.this.board.setSquareStoneByTurn(aiMove.getXPos(),
									aiMove.getYPos(), Game.TURN_GOTE);
							AIGame.this.board.setSquareIconByTurn(aiMove.getXPos(),
									aiMove.getYPos(), Game.TURN_GOTE);
						} catch (InvalidIndexException e) {
							// Normally this wouldn't happen, but it is good
							// to know its call trace when this indeed happens.
							e.printStackTrace();
						}
					} else if (board.isFrozen()) {
						return;
					}

					try {
						Thread.sleep(animationInterval);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		};

		coordinator = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (senteThread.isAlive())
						System.out.println("sente thread is alive");
					if (goteThread.isAlive())
						System.out.println("gote thread is alive");
					if (!isNullMove(senteLastMove)) {
						try {
							senteEngine.updateBoardForAnalysis(senteLastMove, true);
							goteEngine.updateBoardForAnalysis(senteLastMove, false);
							synchronized(senteLastMove) {
								senteLastMove = getNullMove();
							}
							if (AIGame.this.board.doEndGameCheck()) {
								return;
							}
							// active player should only be updated when lastMoves are made onto
							// the analysis boards of each game engine.
							updateActivePlayerWithLock(false);
						} catch (InvalidIndexException e) {
							// still check to see why the board update failed
							e.printStackTrace();
						}
					} else if (!isNullMove(goteLastMove)) {
						System.out.println("Is not null!");
						try {
							senteEngine.updateBoardForAnalysis(goteLastMove, false);
							goteEngine.updateBoardForAnalysis(goteLastMove, true);
							synchronized(goteLastMove) {
								goteLastMove = getNullMove();
							}
							if (AIGame.this.board.doEndGameCheck()) {
								return;
							}
							updateActivePlayerWithLock(true);
						} catch (InvalidIndexException e) {
							e.printStackTrace();
						}
					}

					// NOTE!!! sleep is necessary because the thread scheduler does not
					// switch context very often.
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		};

		senteThread.start();
		goteThread.start();
		coordinator.start();
	}

	@Override
	protected void addGiveUpButtonListener() {}

	@Override
	protected void addStartButtonListener(JButton btn) {}

	/**
	 * Updates the active player when there exists lock on it.
	 */
	private void updateActivePlayerWithLock(boolean active) {
		synchronized(lock) {
			activePlayer = active;
		}
	}

	private BoardLocation getNullMove() {
		return new BoardLocation(-1, -1);
	}

	private boolean isNullMove(BoardLocation move) {
		return move.getXPos() == -1 && move.getYPos() == -1;
	}

	@Override
	public void gameEnd() {
		// TODO kill previous threads and introduce new thread
		super.gameEnd();
		senteLastMove = getNullMove();
		goteLastMove = getNullMove();
		activePlayer = true;
		senteEngine.endGameCleanup();
		goteEngine.endGameCleanup();
		board.cleanUp();
		automaticStart();
	}
}
