package lmh.gomoku.application;

import javax.swing.JButton;
import javax.swing.JLabel;

import lmh.gomoku.application.Game;

public class AIGame extends Game {
	/**
	 * Animation interval in milliseconds.
	 */
	private static int animationInterval = 1000;
	public AIGame() {
		super();
		board.freeze();
		JLabel titleLabel = new JLabel("AI Game");
		titleLabel.setFont(smallGameFont);
		titlePanel.add(titleLabel);
		automaticStart();
	}

	private void automaticStart() {

	}

	@Override
	protected void addGiveUpButtonListener() {}

	@Override
	protected void addStartButtonListener(JButton btn) {}
}
