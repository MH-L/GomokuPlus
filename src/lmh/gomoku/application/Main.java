package lmh.gomoku.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import renju.com.lmh.application.Game.Difficulty;
import lmh.gomoku.application.Game;
import lmh.gomoku.exception.StorageException;
import lmh.gomoku.localStorage.StorageManager;

/**
 * Main class and entry point of the gomoku game
 * (Other main classes are for game server and
 * authentication services). Do note that some of
 * the game's functionalities are not exposed.
 * @author Minghao
 *
 */
public class Main {
	private static Game game;
	/**
	 * IMPORTANT!!!
	 * Please follow this convention (for this project):
	 * if the constant is a primitive type, then use all
	 * caps with underscore, if the constant is an object,
	 * use camelCase.
	 */
	private static final Dimension textFieldDimension = new Dimension(200, 54);
	private static final Dimension horizontalLineDimension = new Dimension(500, 3);
	private static final Dimension horizontalLineShortDimension = new Dimension(360, 3);
	private static final Dimension signUpLoginBtnDimension = new Dimension(200, 70);
	private static final Font loginHintFont = new Font("Calibri", Font.PLAIN, 36);
	private static final Font signUpHintFont = new Font("Tahoma", Font.PLAIN, 36);
	private static final Font signUpBtnFont = new Font("Tahoma", Font.PLAIN, 40);
	private static final Font panelSubTitleFont = new Font("Tahoma", Font.PLAIN, 35);
	private static final Font radioBtnsFont = new Font("Calibri", Font.PLAIN, 29);
	private static final Border panelEmptyBorder = new EmptyBorder(20, 20, 20, 20);
	/**
	 * The reason why we need different lengths for the text fields is that
	 * each grid in GridLayout always has the same grid width. Since the text
	 * is not long enough, setting the length of the textfield too long will
	 * create too much blank space.
	 */
	private static final int TEXTFIELD_LENGTH_BORDER_LAYOUT = 13;
	private static final int TEXTFIELD_LENGTH_GRID_LAYOUT = 10;
	private static final int ROUND_CORNOR_RADIUS = 8;
	public static void main(String args[]) {
		UIManager.put("OptionPane.messageFont", Game.smallGameFont);
		UIManager.put("OptionPane.buttonFont", Game.smallGameFont);
		displayWelcomeFrame();
	}

	/**
	 * Displays the welcome frame on game startup.
	 */
	protected static void displayWelcomeFrame() {
		System.out.println("Welcome to Gomoku Plus! Not a command line application anymore :)");
		JFrame frame = new JFrame("Gomoku Plus");
		frame.setSize(Game.defaultFrameDimension);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		Game.addCloseConfirmation(frame);
		JPanel btnPanel = new JPanel();
		frame.add(btnPanel);
		JButton singleplayerBtn = getPlainLookbtn(
				"Singleplayer", "Open Sans", 28, Font.PLAIN, Color.CYAN);
		JButton multiplayerBtn = getPlainLookbtn(
				"Multiplayer", "Open Sans", 28, Font.PLAIN, Color.YELLOW);
		JButton networkBtn = getPlainLookbtn(
				"Network", "Open Sans", 28, Font.PLAIN, Color.RED);
		JButton aiGameBtn = getPlainLookbtn("AI Game", "Open Sans", 28,
				Font.PLAIN, Color.GRAY);
		btnPanel.add(singleplayerBtn);
		btnPanel.add(multiplayerBtn);
		btnPanel.add(networkBtn);
		btnPanel.add(aiGameBtn);
		try {
			StorageManager.initializeStorage();
		} catch (StorageException e1) {
			JOptionPane.showMessageDialog(null,
					"Unable to initialize game storage. Stats services and online\n"
					+ "authentication services will not be available. The game\n"
					+ "directory should be in C:\\Users\\<YOUR-NAME>\\Gomoku Plus\n"
					+ "Please check your disk.");
		}
		singleplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popSinglePlayerGameOptionWindow(frame);
				// to be implemented later.
//				displayUnimplementedMessage();
			}
		});

		multiplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new MultiplayerGame();
				frame.dispose();
			}
		});

		networkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayLoginPage(frame);
			}
		});

		aiGameBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new AIGame();
				frame.dispose();
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

	/**
	 * Constructs a user login frame for the network game.
	 * In order to start a network game, the user has to
	 * have a game account. This also prevents the game from
	 * being hacked.
	 * @param welcomeFrame The frame to display login message.
	 */
	public static void displayLoginPage(JFrame welcomeFrame) {
		JFrame loginFrame = new JFrame("Login");
		loginFrame.setSize(600, 500);
		loginFrame.setVisible(true);
		JPanel loginPanel = new JPanel();
		loginPanel.setBorder(panelEmptyBorder);
		JLabel label = new JLabel("Log In");
		label.setFont(new Font("Calibri", Font.PLAIN, 58));
		loginPanel.add(label);
		JSeparator panelSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		panelSeparator.setPreferredSize(horizontalLineDimension);
		loginPanel.add(panelSeparator);
		JLabel usernameHint = new JLabel("UserName");
		JLabel passwordHint = new JLabel("Password");
		usernameHint.setFont(loginHintFont);
		passwordHint.setFont(loginHintFont);
		usernameHint.setBorder(new EmptyBorder(20, 0, 20, 0));
		passwordHint.setBorder(new EmptyBorder(20, 0, 20, 0));
		JTextField usernameField = new JTextField(TEXTFIELD_LENGTH_BORDER_LAYOUT);
		usernameField.setPreferredSize(textFieldDimension);
		JTextField passwordField = new JPasswordField(TEXTFIELD_LENGTH_BORDER_LAYOUT);
		passwordField.setPreferredSize(textFieldDimension);
		passwordField.setFont(Game.smallGameFont);
		usernameField.setFont(Game.smallGameFont);
		passwordField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		usernameField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		loginPanel.add(usernameHint);
		loginPanel.add(usernameField);
		loginPanel.add(passwordHint);
		loginPanel.add(passwordField);
		loginFrame.add(loginPanel);
		JButton loginBtn = new JButton("Log In");
		JButton signUpBtn = new JButton("Sign Up");
		loginBtn.setFont(new Font("Calibri Light", Font.PLAIN, 40));
		loginBtn.setPreferredSize(new Dimension(200, 70));
		loginBtn.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		loginBtn.setBackground(new Color(255, 255, 204));
		signUpBtn.setFont(new Font("Calibri Light", Font.ITALIC, 40));
		signUpBtn.setPreferredSize(new Dimension(200, 70));
		signUpBtn.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		signUpBtn.setBackground(new Color(204, 255, 255));
		loginPanel.add(loginBtn);
		loginPanel.add(signUpBtn);

		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loginFrame.dispose();
					game = new NetworkGame();
				} catch (InterruptedException e1) {
					JOptionPane.showMessageDialog(welcomeFrame, "Unable to process network game due to"
							+ " internal error.\nSorry for the inconvenience.",
							"Internal Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				welcomeFrame.dispose();
			}
		});

		signUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loginFrame.dispose();
				displaySignUpFrame();
			}
		});
	}

	/**
	 * This method displays the signup frame. A user
	 * has to have a invitation code in order to sign up.
	 * The reason why that is done is because it prevents
	 * people from registering too many game accounts.
	 * (The server capacity is too limited TBH.)
	 */
	private static void displaySignUpFrame() {
		JFrame signUpFrame = new JFrame("Sign Up");
		signUpFrame.setVisible(true);
		signUpFrame.setSize(new Dimension(650, 650));
		JPanel signUpPanel = new JPanel();
		signUpPanel.setBorder(new EmptyBorder(20, 5, 20, 5));
		signUpFrame.add(signUpPanel);
		JLabel signUpText = new JLabel("Sign Up");
		signUpText.setBorder(new EmptyBorder(0, 0, 20, 0));
		signUpText.setFont(new Font("Tahoma", Font.PLAIN, 48));
		signUpPanel.add(signUpText);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(horizontalLineDimension);
		signUpPanel.add(sep);
		JLabel usernameLabel = new JLabel("UserName");
		usernameLabel.setFont(signUpHintFont);
		JLabel emailLabel = new JLabel("Email");
		emailLabel.setFont(signUpHintFont);
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setFont(signUpHintFont);
		JLabel credentialLabel = new JLabel("Invitation Code");
		credentialLabel.setFont(signUpHintFont);
		JTextField usernameField = new JTextField(TEXTFIELD_LENGTH_GRID_LAYOUT);
		usernameField.setMaximumSize(textFieldDimension);
		usernameField.setFont(Game.smallGameFont);
		JTextField emailField = new JTextField(TEXTFIELD_LENGTH_GRID_LAYOUT);
		emailField.setMaximumSize(textFieldDimension);
		emailField.setFont(Game.smallGameFont);
		JTextField passwordField = new JPasswordField(TEXTFIELD_LENGTH_GRID_LAYOUT);
		passwordField.setMaximumSize(textFieldDimension);
		passwordField.setFont(Game.smallGameFont);
		JTextField invitationField = new JPasswordField(TEXTFIELD_LENGTH_GRID_LAYOUT);
		invitationField.setMaximumSize(textFieldDimension);
		invitationField.setFont(Game.smallGameFont);
		usernameField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		emailField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		passwordField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		invitationField.setBorder(new RoundedBorder(ROUND_CORNOR_RADIUS));
		JPanel gridPanel = new JPanel(new GridLayout(4, 2, 20, 15));

		// Add elements to the grid panel. The panel is 2*4.
		gridPanel.setBorder(panelEmptyBorder);
		gridPanel.add(usernameLabel);
		gridPanel.add(usernameField);
		gridPanel.add(emailLabel);
		gridPanel.add(emailField);
		gridPanel.add(passwordLabel);
		gridPanel.add(passwordField);
		gridPanel.add(credentialLabel);
		gridPanel.add(invitationField);
		signUpPanel.add(gridPanel);
		JButton signUpBtn = new JButton("Register");
		JButton helpBtn = new JButton("Help");
		signUpBtn.setFont(signUpBtnFont);
		helpBtn.setFont(signUpBtnFont);
		signUpPanel.add(signUpBtn);
		signUpPanel.add(helpBtn);
		signUpBtn.setPreferredSize(signUpLoginBtnDimension);
		signUpBtn.setBackground(new Color(204, 255, 204));
		signUpBtn.setBorder(new EmptyBorder(20, 20, 20, 20));
		helpBtn.setPreferredSize(signUpLoginBtnDimension);
		helpBtn.setBackground(new Color(255, 229, 204));
		helpBtn.setBorder(new EmptyBorder(20, 20, 20, 20));
		helpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String helpMessage = String.format(
						"%s\n%s\n%s\n%s\n%s\n%s\n%s",
						"An invitation code is needed to ensure",
						"nobody is registering his/her account",
						"for fun. You do have a better chance to",
						"get the invitation code if you know the",
						"author of the game very well. So, buy him",
						"food and ask him for it. I am sure he will",
						"give it to you.");
				JOptionPane.showMessageDialog(null, helpMessage, "Help",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	/**
	 * Custom class for round-cornered borders for textfields and buttons.
	 * @author Minghao
	 *
	 */
	private static class RoundedBorder implements Border {
	    private int radius;
	    private RoundedBorder(int radius) {
	        this.radius = radius;
	    }

	    @Override
		public Insets getBorderInsets(Component c) {
	        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
	    }

	    @Override
		public boolean isBorderOpaque() {
	        return true;
	    }

	    @Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
	    }
	}

	/**
	 * This message is used when the functionality is under development or
	 * there are bugs in it.
	 */
	public static void displayUnimplementedMessage() {
		JOptionPane.showMessageDialog(null, "The functionality is not implemented yet."
				+ "Our developers\nare working hard on it! Stay tuned!", "Sorry -- Unimplemented",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the option frame for singleplayer game.
	 * Prompt the user for turn and difficulty of the game.
	 * If choices valid then start singlePlayerGame and dispose the
	 * welcome frame.
	 */
	private static void popSinglePlayerGameOptionWindow(JFrame welcomeFrame) {
		JFrame singlePlayerOptionFrame = new JFrame("Options");
		singlePlayerOptionFrame.setVisible(true);
		// TODO Start game if valid choices entered.
//		game = new SingleplayerGame(4, Game.TURN_SENTE);
		singlePlayerOptionFrame.setSize(600, 800);
		JPanel singlePlayerOptionPanel = new JPanel();
		BoxLayout optionLayout = new BoxLayout(singlePlayerOptionPanel, BoxLayout.Y_AXIS);
		singlePlayerOptionPanel.setLayout(optionLayout);

		singlePlayerOptionFrame.add(singlePlayerOptionPanel);
		singlePlayerOptionPanel.setBorder(new EmptyBorder(20, 5, 20, 5));
		JLabel titleLabel = new JLabel("Game Options");
		titleLabel.setBorder(new EmptyBorder(0,0,20,0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 48));
		singlePlayerOptionPanel.add(titleLabel);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
//		sep.setPreferredSize(horizontalLineDimension);
//		singlePlayerOptionPanel.add(sep);
		UIManager.put("RadioButton.font", radioBtnsFont);
		// TODO make this thing look nicer!!!!!!
		JLabel chooseTurn = new JLabel("Choose your turn");
		chooseTurn.setFont(panelSubTitleFont);
		JLabel chooseDiff = new JLabel("Choose your difficulty");
		chooseDiff.setFont(panelSubTitleFont);
		JRadioButton senteOption = new JRadioButton("First");
		JRadioButton goteOption = new JRadioButton("Second");
		JRadioButton noviceDiffOption = new JRadioButton("Novice");
		JRadioButton intermediateDiffOption = new JRadioButton("Intermediate");
		JRadioButton advancedDiffOption = new JRadioButton("Advanced (slow)");
		JRadioButton ultimateDiffOption = new JRadioButton("Ultimate (very slow)");
		JRadioButton mysteriousButton = new JRadioButton("Mysterious");
		singlePlayerOptionPanel.add(chooseTurn);
		singlePlayerOptionPanel.add(senteOption);
		singlePlayerOptionPanel.add(goteOption);
		chooseTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
		goteOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		senteOption.setAlignmentX(Component.CENTER_ALIGNMENT);

		JSeparator buttonSep = new JSeparator(SwingConstants.HORIZONTAL);
//		buttonSep.setPreferredSize(horizontalLineShortDimension);
		JPanel turnOptionPanel = new JPanel();
//		singlePlayerOptionPanel.add(buttonSep);
		singlePlayerOptionPanel.add(chooseDiff);
		singlePlayerOptionPanel.add(noviceDiffOption);
		singlePlayerOptionPanel.add(intermediateDiffOption);
		singlePlayerOptionPanel.add(advancedDiffOption);
		singlePlayerOptionPanel.add(ultimateDiffOption);
		singlePlayerOptionPanel.add(mysteriousButton);
		chooseDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
		noviceDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		intermediateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		advancedDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		ultimateDiffOption.setAlignmentX(Component.CENTER_ALIGNMENT);
		mysteriousButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// create a group so that only one difficulty level is selected
		ButtonGroup difficultyGroup = new ButtonGroup();
		difficultyGroup.add(noviceDiffOption);
		difficultyGroup.add(intermediateDiffOption);
		difficultyGroup.add(advancedDiffOption);
		difficultyGroup.add(ultimateDiffOption);
		difficultyGroup.add(mysteriousButton);

		// select two default things.
		intermediateDiffOption.setSelected(true);
		senteOption.setSelected(true);

		// create a group so that only one turn is selected
		ButtonGroup turnGroup = new ButtonGroup();
		turnGroup.add(senteOption);
		turnGroup.add(goteOption);

		JButton playButton = Main.getPlainLookbtn("Play!", "Calibri", 33, Font.PLAIN, Color.CYAN);
		singlePlayerOptionPanel.add(playButton);
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO get user's choices
				singlePlayerOptionFrame.dispose();
				boolean isSente = false;
				if (senteOption.isSelected())
					isSente = true;
				if (noviceDiffOption.isSelected())
					game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
						Game.TURN_GOTE, Difficulty.NOVICE);
				else if (intermediateDiffOption.isSelected())
					game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
						Game.TURN_GOTE, Difficulty.INTERMEDIATE);
				else if (advancedDiffOption.isSelected())
					game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
						Game.TURN_GOTE, Difficulty.ADVANCED);
				else if (ultimateDiffOption.isSelected())
					game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
						Game.TURN_GOTE, Difficulty.ULTIMATE);
				else {
					int randNum = new Random().nextInt(4);
					if (randNum == 0)
						game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
							Game.TURN_GOTE, Difficulty.NOVICE);
					else if (randNum == 1)
						game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
							Game.TURN_GOTE, Difficulty.INTERMEDIATE);
					else if (randNum == 2)
						game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
							Game.TURN_GOTE, Difficulty.ADVANCED);
					else
						game = new SingleplayerGame(4, isSente ? Game.TURN_SENTE :
							Game.TURN_GOTE, Difficulty.ULTIMATE);
				}
				welcomeFrame.dispose();
			}
		});
	}
}
