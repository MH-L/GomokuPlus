package application;

import javax.swing.JButton;
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
