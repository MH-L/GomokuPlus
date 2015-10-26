package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public abstract class Game {
	protected JPanel mainPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;
	protected JPanel titlePanel;
	protected JMenuBar menuBar;
	protected JPanel boardPanel;
	protected JPanel historyPanel;
	protected JPanel buttonPanel;

	public Game() {
		mainPanel = new JPanel();
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(1000, 600);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 28, Font.PLAIN, Color.RED);
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		historyPanel = new JPanel();
		boardPanel = new JPanel();
		buttonPanel = new JPanel(new GridLayout());
		menuBar = createJMenuBar();
		mainPanel.add(menuBar, BorderLayout.NORTH);
		mainPanel.add(btnStart);
		mainPanel.add(btnGiveUp);
	}

	private static JMenuBar createJMenuBar() {
		JMenuBar menus = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setPreferredSize(new Dimension(166, 60));
		gameMenu.setFont(new Font("Open Sans", Font.PLAIN, 28));
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setPreferredSize(new Dimension(166, 60));
		helpMenu.setFont(new Font("Open Sans", Font.PLAIN, 28));
		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setPreferredSize(new Dimension(168, 60));
		optionsMenu.setFont(new Font("Open Sans", Font.PLAIN, 28));
		menus.add(gameMenu);
		menus.add(helpMenu);
		menus.add(optionsMenu);
		menus.setPreferredSize(new Dimension(500, 60));
		return menus;
	}
}
