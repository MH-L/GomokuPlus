package lmh.gomoku.application;

import javax.swing.JButton;
import javax.swing.JLabel;

import renju.com.lmh.application.Game.Difficulty;
import lmh.gomoku.application.Game;

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
	 * Indicating if the board needs update. Board needs update only when 
	 * one of the computer players finishes making its move and send the result
	 * to shared variables.
	 */
	private boolean boardNeedsUpdate = false;
	/**
	 * Lock for boardNeedsUpdate boolean variable.
	 */
	private Object mutex = new Object();
	
	public AIGame(Difficulty senteDiff, Difficulty goteDiff) {
		super();
		board.freeze();
		JLabel titleLabel = new JLabel("AI Game");
		titleLabel.setFont(smallGameFont);
		titlePanel.add(titleLabel);
		renju.com.lmh.model.Board senteAnalysisBoard = new renju.com.lmh.model.Board(15);
		renju.com.lmh.model.Board goteAnalysisBoard = new renju.com.lmh.model.Board(15);
		senteEngine = new GameEngine(senteDiff, senteAnalysisBoard, true);
		goteEngine = new GameEngine(goteDiff, goteAnalysisBoard, false);
		automaticStart();
	}

	private void automaticStart() {
		senteThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (activePlayer) {
						// time to make move when active player is true
						synchronized(lock) {
							activePlayer = false;
						}
					}
				}
			}
		};
		
		goteThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!activePlayer) {
						// time to make move when active player is false
						synchronized(lock) {
							activePlayer = true;
						}
					}
				}
			}
		};
		
		coordinator = new Thread() {
			@Override
			public void run() {
				while (true) {
					
				}
			}
		};
		
		senteThread.start();
		goteThread.start();
	}

	@Override
	protected void addGiveUpButtonListener() {}

	@Override
	protected void addStartButtonListener(JButton btn) {}
}
