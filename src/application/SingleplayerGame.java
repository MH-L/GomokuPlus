package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SingleplayerGame extends Game {
	/**
	 * Specifies the maximum number of withdrawals a player can get.
	 * Player gets higher score if he/she uses less withdrawals.
	 */
	private static final int MAX_NUM_WITHDRAWAL = 5;
	private static int withdrawal_left;
	private JButton btnWithdrawal;
	private JButton btnHint;
	public SingleplayerGame(int max_num_withdrawal) {
		super();
		this.withdrawal_left = max_num_withdrawal;
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 23, Font.PLAIN, Color.GRAY);
		btnWithdrawal.setMargin(new Insets(0,0,0,0));
		this.btnHint = Main.getPlainLookbtn("Hint", "Open Sans", 23, Font.PLAIN, Color.PINK);
		btnHint.setMargin(new Insets(0,0,0,0));
		this.buttonPanel.add(btnWithdrawal);
		this.buttonPanel.add(btnHint);
		JLabel titleLabel = new JLabel("<html>Singleplayer<br>Game</html>");
		titleLabel.setFont(new Font("Open Sans", Font.PLAIN, 40));
		this.titlePanel.add(titleLabel);
	}
}
