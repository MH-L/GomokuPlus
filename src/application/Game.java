package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class Game {
	protected JPanel mainPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;
	protected JPanel titlePanel;

	public Game() {
		mainPanel = new JPanel(new GridLayout());
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(1000, 600);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 28, Font.PLAIN, Color.RED);
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel.add(btnStart);
		mainPanel.add(btnGiveUp);
	}
}
