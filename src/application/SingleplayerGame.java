package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import Model.Board;
import Model.Coordinate;

public class SingleplayerGame extends Game {
	/**
	 * Specifies the maximum number of withdrawals a player can get. Player gets
	 * higher score if he/she uses less withdrawals.
	 */
	private static final int MAX_NUM_WITHDRAWAL = 5;
	private static int withdrawalLeft;
	private JButton btnWithdrawal;
	private JButton btnHint;

	public SingleplayerGame(int max_num_withdrawal) {
		super();
		withdrawalLeft = max_num_withdrawal;
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 23,
				Font.PLAIN, Color.GRAY);
		btnWithdrawal.setMargin(new Insets(0, 0, 0, 0));
		this.btnWithdrawal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				withdraw();
			}
		});
		this.btnHint = Main.getPlainLookbtn("Hint", "Open Sans", 23,
				Font.PLAIN, Color.PINK);
		btnHint.setMargin(new Insets(0, 0, 0, 0));
		btnHint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (board.isFrozen() || board.isBoardFull()) {
					return;
				}
				Coordinate cor = Board.findEmptyLocSpiral();
				Timer flashSet = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						if (cor.getBackground().equals(Color.YELLOW)) {
							cor.setBackground(Color.GREEN);
						} else {
							cor.setBackground(Color.YELLOW);
							((Timer) event.getSource()).stop();
						}
					}
				});
				flashSet.setInitialDelay(0);
				flashSet.start();
			}
		});
		buttonPanel.add(btnWithdrawal);
		buttonPanel.add(btnHint);
		JLabel titleLabel = new JLabel("<html>Singleplayer<br>Game</html>");
		titleLabel.setFont(new Font("Open Sans", Font.PLAIN, 40));
		titlePanel.add(titleLabel);
	}

	public void withdraw() {
		if (withdrawalLeft <= 0) {
			displayWithdrawFailed();
			return;
		}
		if (board.withdraw()) {
			withdrawalLeft--;
		} else {
			displayWithdrawFailed();
		}
	}
}
