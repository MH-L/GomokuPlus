package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Main {
	private static Game game;
	public static void main(String args[]) {
		UIManager.put("OptionPane.messageFont", Game.smallGameFont);
		UIManager.put("OptionPane.buttonFont", Game.smallGameFont);
		displayWelcomeFrame();
	}

	/**
	 * Displays the welcome frame on game startup.
	 */
	private static void displayWelcomeFrame() {
		JFrame frame = new JFrame("Gomoku Plus");
		frame.setSize(Game.defaultFrameDimension);
		frame.setVisible(true);
		frame.setResizable(false);
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
		singleplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new SingleplayerGame(4);
				((JFrame) singleplayerBtn.getTopLevelAncestor()).dispose();
			}
		});

		multiplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new MultiplayerGame();
				((JFrame) multiplayerBtn.getTopLevelAncestor()).dispose();
			}
		});

		networkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new NetworkGame();
				((JFrame) networkBtn.getTopLevelAncestor()).dispose();
			}
		});
	}

	/**
	 * Used to create a plain-look button.
	 * @param displayText Text to be displayed on the button.
	 * @param font Font of text on the button.
	 * @param fontSize Size of font.
	 * @param fontStyle Bold, regular, light or italic.
	 * @param color Color of background of the button.
	 * @return JButton created with specifications.
	 */
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
