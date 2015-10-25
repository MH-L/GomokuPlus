package application;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	private static Game game;
	public static void main(String args[]) {
		displayWelcomeFrame();
	}

	private static void displayWelcomeFrame() {
		JFrame frame = new JFrame("Gomoku Plus");
		frame.setSize(1000, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel btnPanel = new JPanel();
		frame.add(btnPanel);
		JButton singleplayerBtn = getPlainLookbtn(
				"Singleplayer", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		JButton multiplayerBtn = getPlainLookbtn(
				"Multiplayer", "Open Sans", 28, Font.PLAIN, Color.YELLOW);
		JButton networkBtn = getPlainLookbtn(
				"Network", "Open Sans", 28, Font.PLAIN, Color.RED);
		btnPanel.add(singleplayerBtn);
		btnPanel.add(multiplayerBtn);
		btnPanel.add(networkBtn);
	}

	protected static JButton getPlainLookbtn(String displayText, String font,
			int fontSize, int fontStyle, Color color) {
		JButton btn = new JButton(displayText);
		btn.setBackground(color);
		btn.setFont(new Font(font, fontStyle, fontSize));
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		return btn;
	}
}
