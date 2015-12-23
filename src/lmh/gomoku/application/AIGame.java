package lmh.gomoku.application;

import javax.swing.JButton;
import javax.swing.JLabel;

import renju.com.lmh.application.Game.Difficulty;
import lmh.gomoku.application.Game;

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
	 * Two AI engines, senteEngine for the computer player who makes
	 * move first, and goteEngine for the computer player who makes move second.
	 */
	private GameEngine senteEngine;
	private GameEngine goteEngine;
	
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
				
			}
		};
		
		goteThread = new Thread() {
			@Override
			public void run() {
				
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
