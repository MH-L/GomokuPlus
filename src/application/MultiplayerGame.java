package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

public class MultiplayerGame extends Game {
	private static int p1WithdrawalLeft = 2;
	private static int p2WithdrawalLeft = 2;
	private JButton btnWithdrawal;
	private JButton btnTie;
	public MultiplayerGame() {
		super();
		JLabel titleLabel = new JLabel("<html>Multiplayer<br>Game</html>");
		titleLabel.setFont(Game.largeGameFont);
		titlePanel.add(titleLabel);
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 23, Font.PLAIN, Color.GRAY);
		btnWithdrawal.setMargin(new Insets(0,0,0,0));
		this.btnWithdrawal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				withdraw();
			}
		});
		this.btnTie = Main.getPlainLookbtn("Hint", "Open Sans", 23, Font.PLAIN, Color.PINK);
		btnTie.setMargin(new Insets(0,0,0,0));
		buttonPanel.add(btnWithdrawal);
		buttonPanel.add(btnTie);
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
			} else {
				p2WithdrawalLeft --;
			}
		} else {
			displayWithdrawFailed();
		}
	}
}
