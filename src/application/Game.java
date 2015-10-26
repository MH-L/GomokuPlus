package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import Model.Board;
import Model.Coordinate;

public abstract class Game {
	protected static final Font smallGameFont = new Font("Open Sans",
			Font.PLAIN, 28);
	protected static final Font largeGameFont = new Font("Open Sans",
			Font.PLAIN, 40);
	public static final Dimension defaultFrameDimension = new Dimension(1000,
			760);
	public static final Dimension defaultFrameSmall = new Dimension(700, 500);
	protected JPanel mainPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;
	protected JPanel titlePanel;
	protected JMenuBar menuBar;
	protected JPanel boardPanel;
	protected JPanel historyPanel;
	protected JPanel buttonPanel;
	protected JPanel functionPanel;
	private Board board;

	public Game() {
		mainPanel = new JPanel(new BorderLayout());
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(defaultFrameDimension);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 23, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 23, Font.PLAIN, Color.RED);
		btnStart.setMargin(new Insets(0,0,0,0));
		btnGiveUp.setMargin(new Insets(0,0,0,0));
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		functionPanel = new JPanel(new BorderLayout());
		functionPanel.setPreferredSize(new Dimension(260, 700));
		buttonPanel = new JPanel(new GridLayout(2, 2));
		buttonPanel.setPreferredSize(new Dimension(260, 200));
		titlePanel = new JPanel(new BorderLayout());
		titlePanel.setPreferredSize(new Dimension(260, 100));
		historyPanel = new JPanel(new BorderLayout());
		functionPanel.add(titlePanel, BorderLayout.NORTH);
		titlePanel.add(new JSeparator());
		functionPanel.add(historyPanel, BorderLayout.CENTER);
		historyPanel.add(new JSeparator());
		functionPanel.add(buttonPanel, BorderLayout.SOUTH);

		boardPanel = new JPanel(new GridLayout(15,15));
		boardPanel.setPreferredSize(new Dimension(700, 700));

		menuBar = createJMenuBar();
		mainFrame.setJMenuBar(menuBar);
		buttonPanel.add(btnStart);
		buttonPanel.add(btnGiveUp);
		mainPanel.add(boardPanel, BorderLayout.LINE_START);
		mainPanel.add(new JSeparator(JSeparator.VERTICAL));
		mainPanel.add(functionPanel, BorderLayout.LINE_END);
		this.board = new Board(boardPanel);
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
		gameMenu.addSeparator();
		JMenuItem exit = new JMenuItem("Exit to main menu");
		exit.setFont(smallGameFont);
		gameMenu.add(exit);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setPreferredSize(new Dimension(166, 60));
		helpMenu.setFont(smallGameFont);
		JMenuItem about = new JMenuItem("About");
		JMenuItem onlineHelp = new JMenuItem("Online Help");
		about.setFont(smallGameFont);
		onlineHelp.setFont(smallGameFont);
		helpMenu.add(about);
		helpMenu.addSeparator();
		helpMenu.add(onlineHelp);

		JMenu optionsMenu = new JMenu("Options");
		optionsMenu.setPreferredSize(new Dimension(168, 60));
		optionsMenu.setFont(smallGameFont);
		JMenuItem soundOption = new JMenuItem("Sound");
		JMenuItem animationOption = new JMenuItem("Animation");
		soundOption.setFont(smallGameFont);
		animationOption.setFont(smallGameFont);
		optionsMenu.add(animationOption);
		optionsMenu.addSeparator();
		optionsMenu.add(soundOption);

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
		JMenuItem stepForward = new JMenuItem("Step Forward");
		JMenuItem stepBackward = new JMenuItem("Step Backward");
		JMenuItem animate = new JMenuItem("Animate");
		stepForward.setFont(smallGameFont);
		stepBackward.setFont(smallGameFont);
		animate.setFont(smallGameFont);
		analysisMenu.add(stepForward);
		analysisMenu.addSeparator();
		analysisMenu.add(stepBackward);
		analysisMenu.addSeparator();
		analysisMenu.add(animate);

		JMenu statsMenu = new JMenu("Stats");
		statsMenu.setPreferredSize(new Dimension(168, 60));
		statsMenu.setFont(smallGameFont);
		JMenuItem clearStats = new JMenuItem("Clear Stats");
		JMenuItem showStats = new JMenuItem("Show Stats");
		clearStats.setFont(smallGameFont);
		showStats.setFont(smallGameFont);
		statsMenu.add(clearStats);
		statsMenu.addSeparator();
		statsMenu.add(showStats);
		showStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showStatsWindow();
			}
		});

		menus.add(gameMenu);
		menus.add(new JSeparator(JSeparator.VERTICAL));
		menus.add(helpMenu);
		menus.add(new JSeparator(JSeparator.VERTICAL));
		menus.add(optionsMenu);
		menus.add(new JSeparator(JSeparator.VERTICAL));
		menus.add(exportMenu);
		menus.add(new JSeparator(JSeparator.VERTICAL));
		menus.add(analysisMenu);
		menus.add(new JSeparator(JSeparator.VERTICAL));
		menus.add(statsMenu);
		menus.setPreferredSize(new Dimension(500, 60));
		return menus;
	}

	private static void showStatsWindow() {
		JFrame statsFrame = new JFrame("Stats");
		statsFrame.setVisible(true);
		statsFrame.setSize(defaultFrameSmall);
		JPanel statsPanel = new JPanel(new GridLayout(3, 2));
		statsPanel.setFont(smallGameFont);
		statsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		statsFrame.add(statsPanel);
		ArrayList<JLabel> lablesToBeAddedToStats = new ArrayList<JLabel>();
		lablesToBeAddedToStats.add(new JLabel("Wins:"));
		lablesToBeAddedToStats.add(new JLabel("13"));
		lablesToBeAddedToStats.add(new JLabel("Losses:"));
		lablesToBeAddedToStats.add(new JLabel("9"));
		lablesToBeAddedToStats.add(new JLabel("Win Percentage"));
		lablesToBeAddedToStats.add(new JLabel("77%"));
		for (JLabel lable : lablesToBeAddedToStats) {
			lable.setFont(largeGameFont);
			lable.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			statsPanel.add(lable);
		}
	}

	public boolean isGameOver() {
		return board.isGameOver();
	}
}
