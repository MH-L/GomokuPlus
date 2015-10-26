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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public abstract class Game {
	protected static final Font smallGameFont = new Font("Open Sans", Font.PLAIN, 28);
	protected static final Font largeGameFont = new Font("Open Sans", Font.PLAIN, 40);
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
		mainFrame.setJMenuBar(menuBar);
		mainPanel.add(btnStart);
		mainPanel.add(btnGiveUp);
	}

	private static JMenuBar createJMenuBar() {
		JMenuBar menus = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGame = new JMenuItem("New Game (F12)");
		newGame.setFont(smallGameFont);
		gameMenu.add(newGame);
		gameMenu.setPreferredSize(new Dimension(166, 60));
		gameMenu.setFont(smallGameFont);
		gameMenu.addSeparator();
		JMenuItem loadGame = new JMenuItem("Load Game (F11)");
		loadGame.setFont(smallGameFont);
		gameMenu.add(loadGame);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setPreferredSize(new Dimension(166, 60));
		helpMenu.setFont(smallGameFont);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setPreferredSize(new Dimension(168, 60));
		optionsMenu.setFont(smallGameFont);

		JMenu exportMenu = new JMenu("Export");
		exportMenu.setPreferredSize(new Dimension(166, 60));
		exportMenu.setFont(smallGameFont);
		JMenuItem exportToTxt = new JMenuItem("Export to .txt");
		exportToTxt.setFont(smallGameFont);
		exportMenu.add(exportToTxt);
		exportMenu.addSeparator();
		JMenuItem exportToXls = new JMenuItem("Export to .xls");
		exportToXls.setFont(smallGameFont);
		exportMenu.add(exportToXls);

		JMenu analysisMenu = new JMenu("Analysis");
		analysisMenu.setPreferredSize(new Dimension(166, 60));
		analysisMenu.setFont(smallGameFont);
		JMenu statsMenu = new JMenu("Stats");
		statsMenu.setPreferredSize(new Dimension(168, 60));
		statsMenu.setFont(smallGameFont);
		menus.add(gameMenu);
		menus.add(helpMenu);
		menus.add(optionsMenu);
		menus.add(exportMenu);
		menus.add(analysisMenu);
		menus.add(statsMenu);
		menus.setPreferredSize(new Dimension(500, 60));
		return menus;
	}
}
