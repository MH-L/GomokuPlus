package application;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game {
	protected JPanel boardPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;

	public Game() {
		boardPanel = new JPanel();
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(1000, 600);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 28, Font.PLAIN, Color.RED);
		mainFrame.add(boardPanel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		boardPanel.add(btnStart);
		boardPanel.add(btnGiveUp);
	}
}
