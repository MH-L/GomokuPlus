package application;

import java.awt.Color;
import java.awt.Font;

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
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 28, Font.PLAIN, Color.GRAY);
		this.btnHint = Main.getPlainLookbtn("Hint", "Open Sans", 28, Font.PLAIN, Color.PINK);
		this.mainPanel.add(btnWithdrawal);
		this.mainPanel.add(btnHint);
		this.titlePanel = new JPanel();
		this.titlePanel.add(new JLabel("Singleplayer Game"));
		this.mainPanel.add(titlePanel);
	}
}
