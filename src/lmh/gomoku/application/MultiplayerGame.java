package lmh.gomoku.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import lmh.gomoku.application.Game;
import lmh.gomoku.application.Main;

public class MultiplayerGame extends Game {
	private static int p1WithdrawalLeft = 2;
	private static int p2WithdrawalLeft = 2;
	private JButton btnWithdrawal;
	private JButton btnTie;
	private JLabel withdrawals;
	private JLabel blackWithdrawals;
	private JLabel whiteWithdrawals;
	public MultiplayerGame() {
		super();
		JLabel titleLabel = new JLabel("<html>Multiplayer<br>Game</html>");
		titlePanel.setPreferredSize(new Dimension(Game.functionPanelWidth, 150));
		titleLabel.setFont(Game.largeGameFont);
		titlePanel.add(titleLabel);
		JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		titleSeparator.setPreferredSize(new Dimension(200, 3));
		titlePanel.add(titleSeparator);
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 23, Font.PLAIN, Color.GRAY);
		btnWithdrawal.setMargin(new Insets(0,0,0,0));
		this.btnWithdrawal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (board.isFrozen()) {
					JOptionPane.showMessageDialog(mainFrame, "The game has not yet started.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				withdraw();
			}
		});
		this.btnTie = Main.getPlainLookbtn("Propose Tie", "Open Sans", 23, Font.PLAIN, Color.PINK);
		this.btnTie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (board.isFrozen()) {
					JOptionPane.showMessageDialog(mainFrame, "The game has not yet started.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int choice = JOptionPane.showConfirmDialog(mainFrame,
						"Other player wants to tie the game. Do you agree?",
						"Proposing tie", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);

				if (choice == JOptionPane.YES_OPTION) {
					displayTieMessageTieAgreed();
					gameEnd();
				} else {

				}
			}
		});
		btnTie.setMargin(new Insets(0,0,0,0));
		buttonPanel.add(btnWithdrawal);
		buttonPanel.add(btnTie);
		withdrawals = new JLabel("Withdrawals Left:");
		blackWithdrawals = new JLabel("Black -- 2");
		whiteWithdrawals = new JLabel("White -- 2");
		withdrawals.setFont(Game.smallGameFont);
		blackWithdrawals.setFont(Game.smallGameFont);
		whiteWithdrawals.setFont(Game.smallGameFont);
		historyPanel.add(withdrawals);
		historyPanel.add(blackWithdrawals);
		historyPanel.add(whiteWithdrawals);
	}

	@Override
	public void withdraw() {
		boolean canWithdraw = (board.getActivePlayer() == 1) ?
				(p1WithdrawalLeft > 0) : (p2WithdrawalLeft > 0);
		if (!canWithdraw) {
			displayWithdrawFailed();
		} else if (this.board.withdraw()) {
			if (board.getActivePlayer() == 1) {
				p1WithdrawalLeft --;
				blackWithdrawals.setText("Black -- " + p1WithdrawalLeft);
			} else {
				p2WithdrawalLeft --;
				whiteWithdrawals.setText("White -- " + p2WithdrawalLeft);
			}
		} else {
			displayWithdrawFailed();
		}
	}

	@Override
	public void restoreWithdrawals() {
		p1WithdrawalLeft = 2;
		p2WithdrawalLeft = 2;
	}
}
