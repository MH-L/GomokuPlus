package lmh.gomoku.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import lmh.gomoku.application.Game;
import lmh.gomoku.application.Main;
import lmh.gomoku.exception.XMLException;
import lmh.gomoku.localStorage.StorageManager;
import lmh.gomoku.model.Board;
import lmh.gomoku.util.RecordCreator;
import lmh.gomoku.util.XMLHelper;
import lmh.gomoku.util.XMLHelper.XMLElement;

public abstract class Game {
	protected static final Font smallGameFont = new Font("Open Sans",
			Font.PLAIN, 28);
	protected static final Font largeGameFont = new Font("Open Sans",
			Font.PLAIN, 40);
	protected static final Font mediumGameFont = new Font("Open Sans",
			Font.PLAIN, 35);
	protected static final Font tinyGameFont = new Font("Open Sans",
			Font.PLAIN, 16);
	public static final Insets emptyMargin = new Insets(0, 0, 0, 0);
	public static final Color boardColor = new Color(204, 204, 0);
	public static final Dimension defaultFrameDimension = new Dimension(1400,
			760);
	public static final Dimension defaultFrameSmall = new Dimension(700, 500);
	public static final int functionPanelWidth = 295;
	public static final int NUM_STONE_TO_WIN = 5;
	public static final int TURN_SENTE = 1;
	public static final int TURN_GOTE = 2;
	protected JPanel mainPanel;
	protected JPanel parentPanel;
	protected JPanel chatPanel;
	protected JFrame mainFrame;
	protected JButton btnStart;
	protected JButton btnGiveUp;
	protected JPanel titlePanel;
	protected JMenuBar menuBar;
	protected JPanel boardPanel;
	protected JPanel historyPanel;
	protected JPanel buttonPanel;
	protected JPanel functionPanel;
	private JLabel gameStarted;
	protected JTextArea messageArea;
	protected Board board;

	/**
	 * Sente -- first player
	 * Gote -- second player
	 * These are originated from Japanese.
	 * @author Minghao
	 *
	 */
	public enum Result {
		UNDECIDED, SENTE, GOTE, TIE
	}

	public Game() {
		this(false, 0);
	}

	public Game(boolean setupAI, int playerTurn) {
		parentPanel = new JPanel(new BorderLayout());
		chatPanel = new JPanel(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(395, 700));
		mainPanel = new JPanel(new BorderLayout());
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(defaultFrameDimension);
		btnStart = Main.getPlainLookbtn("Start!", "Open Sans", 23, Font.PLAIN, Color.CYAN);
		btnGiveUp = Main.getPlainLookbtn("Give UP!", "Open Sans", 23, Font.PLAIN, Color.RED);
		btnStart.setMargin(emptyMargin);
		btnGiveUp.setMargin(emptyMargin);
		parentPanel.add(mainPanel, BorderLayout.WEST);
		parentPanel.add(new JSeparator());
		parentPanel.add(chatPanel, BorderLayout.EAST);
		mainFrame.add(parentPanel);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addCloseConfirmation(mainFrame);

		functionPanel = new JPanel(new BorderLayout());
		functionPanel.setPreferredSize(new Dimension(functionPanelWidth, 700));
		buttonPanel = new JPanel(new GridLayout(2, 2));
		buttonPanel.setPreferredSize(new Dimension(functionPanelWidth, 200));
		titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(functionPanelWidth, 100));
		historyPanel = new JPanel(new GridLayout(4, 1));
		functionPanel.add(titlePanel, BorderLayout.NORTH);
		functionPanel.add(historyPanel, BorderLayout.CENTER);
		functionPanel.add(buttonPanel, BorderLayout.SOUTH);

		gameStarted = new JLabel("Game not yet started.");
		gameStarted.setFont(smallGameFont);
		historyPanel.add(gameStarted);

		boardPanel = new JPanel(new GridLayout(Board.height,Board.width));
		boardPanel.setPreferredSize(new Dimension(700, 700));

		menuBar = createJMenuBar();
		mainFrame.setJMenuBar(menuBar);
		buttonPanel.add(btnStart);
		buttonPanel.add(btnGiveUp);
		mainPanel.add(boardPanel, BorderLayout.LINE_START);
		mainPanel.add(new JSeparator(SwingConstants.VERTICAL));
		mainPanel.add(functionPanel, BorderLayout.LINE_END);

		messageArea = new JTextArea(4, 40);
		messageArea.setFont(smallGameFont);
		chatPanel.add(messageArea, BorderLayout.CENTER);
		if (!setupAI) {
			initialSetUp();
		} else {
			addStartButtonListener(btnStart);
			addGiveUpButtonListener();
			this.board = new Board(boardPanel, this, true, playerTurn);
		}
	}

	protected void initialSetUp() {
		addStartButtonListener(btnStart);
		addGiveUpButtonListener();
		board = new Board(boardPanel, this);
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
				if (gameStarted.getText().equals("Game Started.")) {
					JOptionPane.showMessageDialog(mainFrame, "The game already started.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				gameStart();
			}
		});
	}

	protected void gameStart() {
		board.resetBoard();
		board.activate();
		gameStarted.setText("Game Started.");
		board.startAIThread();
	}

	public void gameEnd() {
		board.cleanUp();
		restoreWithdrawals();
		gameStarted.setText("Game not yet started.");
	}

	protected void restoreWithdrawals() {}

	private JMenuBar createJMenuBar() {
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
				doSocketClose();
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
		JMenuItem chooseFile = new JMenuItem("Choose File...");
		JMenuItem stepForward = new JMenuItem("Step Forward");
		JMenuItem stepBackward = new JMenuItem("Step Backward");
		JMenuItem animate = new JMenuItem("Animate");
		chooseFile.setFont(smallGameFont);
		stepForward.setFont(smallGameFont);
		stepBackward.setFont(smallGameFont);
		animate.setFont(smallGameFont);
		analysisMenu.add(chooseFile);
		analysisMenu.addSeparator();
		analysisMenu.add(stepForward);
		analysisMenu.addSeparator();
		analysisMenu.add(stepBackward);
		analysisMenu.addSeparator();
		analysisMenu.add(animate);
		addAnimateMenuItemListener(animate);
		addStepBackwardMenuItemListener(stepBackward);
		addStepForwardMenuItemListener(stepForward);
		addAnimationConfigurationMenuItemListener(animationOption);

		chooseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return ".xml";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}

						String extension = getExtension(f);
						if (extension != null && extension.equals("xml")) {
							return true;
						}
						return false;
					}

					public String getExtension(File f) {
				        String ext = null;
				        String s = f.getName();
				        int i = s.lastIndexOf('.');

				        if (i > 0 &&  i < s.length() - 1) {
				            ext = s.substring(i+1).toLowerCase();
				        }
				        return ext;
				    }
				});
				int result = fc.showOpenDialog(mainPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
					File fl = fc.getSelectedFile();
					Game g = new AnalysisGame(fl);
					mainFrame.dispose();
				}
			}
		});

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
				String content;
				try {
					System.out.println("start read stats");
					content=readstats(new File(StorageManager.generateStatsFile()));
					showStatsWindow(content);
					System.out.println("finish read stats");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("cannot read stats");
					e.printStackTrace();
					
				}
				
			}
		});

		menus.add(gameMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(helpMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(optionsMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(exportMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(analysisMenu);
		menus.add(new JSeparator(SwingConstants.VERTICAL));
		menus.add(statsMenu);
		menus.setPreferredSize(new Dimension(500, 60));
		return menus;
	}

	protected void addAnimateMenuItemListener(JMenuItem item) {
		item.setEnabled(false);
	}

	protected void addStepForwardMenuItemListener(JMenuItem item) {
		item.setEnabled(false);
	}

	protected void addStepBackwardMenuItemListener(JMenuItem item) {
		item.setEnabled(false);
	}

	protected void addAnimationConfigurationMenuItemListener(JMenuItem item) {
		item.setEnabled(false);
	}

	private void addActionListenerAnalysisOnly(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainFrame,
						"This option is only available for analysis game.");
			}
		});
	}

	protected void doSocketClose() {}
	
	/**
     * This method extract the counts of win/lose/tie in the stats file.
     *
     */ 
	public static int[] extractnumbers(String statscontent){
		int[] counts=new int[3];
		String [] stringArray;
		String str;
		str = statscontent.replaceAll("[^-?0-9]+", " "); 
		stringArray=str.trim().split(" ");
		for(int i=0;i<3;i++){
			counts[i]=Integer.parseInt(stringArray[i]);
		}
		return counts;	  
		
	}
	/**
     * This method loads statsfile from file system and returns the byte array of the content.
     *
     * @param fileName
     * @return
     * @throws Exception
     */ 
	public static String readstats(File statsfile) throws Exception {		 
        byte[] decodedBytes = Base64.getDecoder().decode(loadFileAsBytesArray(statsfile));
        String statscontent= new String(decodedBytes, "UTF-8");
        System.out.println(statscontent);
		return statscontent;
    }
	
	public static byte[] loadFileAsBytesArray(File statsfile) throws Exception {
        int length = (int) statsfile.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(statsfile));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
    }
	
		
	/*private static void showStatsWindow() {
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
	}*/
	
	private static void showStatsWindow(String text) {
		JFrame statsFrame = new JFrame("Stats");
		statsFrame.setVisible(true);
		statsFrame.setSize(defaultFrameDimension);
		JLabel Text = new JLabel(text);  
		Text.setFont(largeGameFont);
		Text.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		statsFrame.add(Text);
		
	}

	public void displayOccupiedWarning() {
		JOptionPane.showMessageDialog(mainFrame, "The square is already occupied.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void errorRendering() {
		JOptionPane.showMessageDialog(mainFrame, "Unable to render board image. Fatal error!",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void displayWinnerInfo(boolean isSente) {
		String winnerInfo = isSente ? "Black" : "White";
		JOptionPane.showMessageDialog(null, winnerInfo + " wins!",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	public void warnGameFrozen() {
		JOptionPane.showMessageDialog(mainFrame, "Game is not yet started or has finished.\nPlease start new game by pressing"
				+ " start\nor go to menu bar.", "Game Status Info", JOptionPane.INFORMATION_MESSAGE);
	}

	public void displayTieMessageBoardFull() {
		JOptionPane.showMessageDialog(mainFrame, "Board Full. Game comes to a tie.",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	public void displayTieMessageTieAgreed() {
		JOptionPane.showMessageDialog(mainFrame, "Tie Agreed. Game comes to a tie.",
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	public void warnNotYourTurn() {
		JOptionPane.showMessageDialog(mainFrame, "Not Your Turn!",
				"Computer Making Move", JOptionPane.WARNING_MESSAGE);
	}

	public static void addCloseConfirmation(JFrame frame) {
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame,
		            "Are you sure to close this window?", "Confirm Closing",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		            System.exit(0);
		        }
		    }
		});
	}

	public void displayWithdrawFailed() {
		JOptionPane.showMessageDialog(mainFrame, "You have run out of your withdrawals"
				+ " or there is\n nothing to withdraw.",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	public void withdraw() {}
}
