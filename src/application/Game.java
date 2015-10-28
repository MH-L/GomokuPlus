package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
	public static final int functionPanelWidth = 295;
	public static final int NUM_STONE_TO_WIN = 5;
	protected static JPanel mainPanel;
	protected static JFrame mainFrame;
	protected static JButton btnStart;
	protected static JButton btnGiveUp;
	protected static JPanel titlePanel;
	protected static JMenuBar menuBar;
	protected static JPanel boardPanel;
	protected static JPanel historyPanel;
	protected static JPanel buttonPanel;
	protected static JPanel functionPanel;
	private static JLabel gameStarted;
	protected static Board board;

	/**
	 * Sente -- first player
	 * Gote -- second player
	 * These are originated from Japanese.
	 * @author Minghao
	 *
	 */
	public enum Result {
		UNDECIDED, SENTE, GOTE
	}

	public Game() {
		mainPanel = new JPanel(new BorderLayout());
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(defaultFrameDimension);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 23, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 23, Font.PLAIN, Color.RED);
		btnStart.setMargin(new Insets(0,0,0,0));
		btnGiveUp.setMargin(new Insets(0,0,0,0));
		addStartButtonListener(btnStart);
		addGiveUpButtonListener();
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addCloseConfirmation(mainFrame);

		functionPanel = new JPanel(new BorderLayout());
		functionPanel.setPreferredSize(new Dimension(functionPanelWidth, 700));
		buttonPanel = new JPanel(new GridLayout(2, 2));
		buttonPanel.setPreferredSize(new Dimension(functionPanelWidth, 200));
		titlePanel = new JPanel(new BorderLayout());
		titlePanel.setPreferredSize(new Dimension(functionPanelWidth, 100));
		historyPanel = new JPanel(new BorderLayout());
		functionPanel.add(titlePanel, BorderLayout.NORTH);
		titlePanel.add(new JSeparator());
		functionPanel.add(historyPanel, BorderLayout.CENTER);
		historyPanel.add(new JSeparator());
		functionPanel.add(buttonPanel, BorderLayout.SOUTH);

		gameStarted = new JLabel("Game not yet started.");
		gameStarted.setFont(smallGameFont);
		historyPanel.add(gameStarted);

		boardPanel = new JPanel(new GridLayout(15,15));
		boardPanel.setPreferredSize(new Dimension(700, 700));

		menuBar = createJMenuBar();
		mainFrame.setJMenuBar(menuBar);
		buttonPanel.add(btnStart);
		buttonPanel.add(btnGiveUp);
		mainPanel.add(boardPanel, BorderLayout.LINE_START);
		mainPanel.add(new JSeparator(JSeparator.VERTICAL));
		mainPanel.add(functionPanel, BorderLayout.LINE_END);
		board = new Board(boardPanel);
	}

	protected void addGiveUpButtonListener() {
		btnGiveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (board.isFrozen()) {
					JOptionPane.showMessageDialog(mainFrame, "The game has not yet started.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (board.getActivePlayer() == 1) {
					JOptionPane.showMessageDialog(mainFrame, "Black, you lose.\nWhite, you win!",
							"Game Over", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(mainFrame, "Black, you win!\nWhite, you lose!",
							"Game Over", JOptionPane.INFORMATION_MESSAGE);
				}
				gameEnd();
			}
		});

	}

	protected void addStartButtonListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameStart();
			}
		});
	}

	private static void gameStart() {
		board.resetBoard();
		board.activate();
		gameStarted.setText("Game Started.");
	}

	private static void gameEnd() {
		board.resetBoard();
		board.freeze();
		gameStarted.setText("Game not yet started.");
	}

	private static JMenuBar createJMenuBar() {
		JMenuBar menus = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGame = new JMenuItem("New Game (F12)");
		newGame.setFont(smallGameFont);
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameStart();
			}
		});

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
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
				Main.displayWelcomeFrame();
			}
		});

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

	public static void displayOccupiedWarning() {
		JOptionPane.showMessageDialog(mainFrame, "The square is already occupied.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void errorRendering() {
		JOptionPane.showMessageDialog(mainFrame, "Unable to render board image. Fatal error!",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void displayWinnerInfo(boolean isSente) {
		String winnerInfo = isSente ? "Black" : "White";
		JOptionPane.showMessageDialog(mainFrame, winnerInfo + " wins!",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
		gameEnd();
	}

	public static void warnGameFrozen() {
		JOptionPane.showMessageDialog(mainFrame, "Game is finished. Please start new game by pressing"
				+ " start or go to menu bar.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void displayTieMessage() {
		JOptionPane.showMessageDialog(mainFrame, "Board Full. Game comes to a tie.",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
		gameEnd();
	}

	public static void addCloseConfirmation(JFrame frame) {
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame,
		            "Are you sure to close this window?", "Confirm Closing",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		            System.exit(0);
		        }
		    }
		});
	}

	public static void displayWithdrawFailed() {
		JOptionPane.showMessageDialog(mainFrame, "You have run out of your withdrawals"
				+ " or there is nothing to withdraw.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void withdraw() {}
}
