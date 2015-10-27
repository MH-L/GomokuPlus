package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
	}

	@Override
	protected void addGiveUpButtonListener() {}

	@Override
	protected void addStartButtonListener(JButton btn) {}
}
