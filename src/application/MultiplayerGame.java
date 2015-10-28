package application;

import javax.swing.JLabel;

public class MultiplayerGame extends Game {
	private static int p1WithdrawalLeft = 2;
	private static int p2WithdrawalLeft = 2;
	public MultiplayerGame() {
		super();
		JLabel titleLabel = new JLabel("<html>Multiplayer<br>Game</html>");
		titleLabel.setFont(Game.largeGameFont);
		this.titlePanel.add(titleLabel);
	}
}
