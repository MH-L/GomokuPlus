package application;

import javax.swing.JLabel;

public class MultiplayerGame extends Game {
	public MultiplayerGame() {
		super();
		JLabel titleLabel = new JLabel("<html>Multiplayer<br>Game</html>");
		titleLabel.setFont(Game.largeGameFont);
		this.titlePanel.add(titleLabel);
	}
}
