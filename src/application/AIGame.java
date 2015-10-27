package application;

import javax.swing.JLabel;

public class AIGame extends Game {
	/**
	 * Animation interval in milliseconds.
	 */
	private static int animationInterval = 1000;
	public AIGame() {
		super();
		board.freeze();
		JLabel titleLabel = new JLabel("AI Game");
	}
}
