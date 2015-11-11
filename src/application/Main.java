package application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

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
	protected static void displayWelcomeFrame() {
		System.out.println("Welcome to Gomoku Plus! Not a command line application anymore :)");
		JFrame frame = new JFrame("Gomoku Plus");
		frame.setSize(Game.defaultFrameDimension);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		singleplayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game = new SingleplayerGame(4);
				frame.dispose();
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
				displayLoginPage();
//				try {
//					game = new NetworkGame();
//				} catch (InterruptedException e1) {
//					JOptionPane.showMessageDialog(frame, "Unable to process network game due to"
//							+ " internal error.\nSorry for the inconvenience.",
//							"Internal Error", JOptionPane.ERROR_MESSAGE);
//					e1.printStackTrace();
//				}
//				frame.dispose();
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

	public static void displayLoginPage() {
		JFrame loginFrame = new JFrame("Login");
		loginFrame.setSize(600, 500);
		loginFrame.setVisible(true);
		JPanel loginPanel = new JPanel();
		loginPanel.setBorder(new EmptyBorder(20, 5, 20, 5));
		JLabel label = new JLabel("Log In");
		label.setFont(new Font("Calibri", Font.PLAIN, 58));
		loginPanel.add(label);
		JSeparator panelSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		panelSeparator.setPreferredSize(new Dimension(450, 3));
		loginPanel.add(panelSeparator);
		JLabel usernameHint = new JLabel("UserName");
		JLabel passwordHint = new JLabel("Password");
		usernameHint.setFont(new Font("Calibri", Font.PLAIN, 36));
		passwordHint.setFont(new Font("Calibri", Font.PLAIN, 36));
		usernameHint.setBorder(new EmptyBorder(20, 0, 20, 0));
		passwordHint.setBorder(new EmptyBorder(20, 0, 20, 0));
		JTextField usernameField = new JTextField(13);
		usernameField.setPreferredSize(new Dimension(200, 44));
		JTextField passwordField = new JPasswordField(13);
		passwordField.setPreferredSize(new Dimension(200, 44));
		passwordField.setFont(Game.smallGameFont);
		usernameField.setFont(Game.smallGameFont);
		loginPanel.add(usernameHint);
		loginPanel.add(usernameField);
		loginPanel.add(Box.createVerticalStrut(20));
		loginPanel.add(passwordHint);
		loginPanel.add(passwordField);
		loginFrame.add(loginPanel);
		JButton loginBtn = new JButton("Log In");
		JButton signUpBtn = new JButton("Sign Up");
		loginBtn.setFont(new Font("Calibri Light", Font.PLAIN, 40));
		loginBtn.setPreferredSize(new Dimension(200, 70));
		loginBtn.setBorder(new RoundedBorder(10));
		loginBtn.setBackground(new Color(255, 255, 204));
		signUpBtn.setFont(new Font("Calibri Light", Font.ITALIC, 40));
		signUpBtn.setPreferredSize(new Dimension(200, 70));
		signUpBtn.setBorder(new RoundedBorder(10));
		signUpBtn.setBackground(new Color(204, 255, 255));
		loginPanel.add(loginBtn);
		loginPanel.add(signUpBtn);
	}

	private static class RoundedBorder implements Border {
	    private int radius;
	    private RoundedBorder(int radius) {
	        this.radius = radius;
	    }

	    public Insets getBorderInsets(Component c) {
	        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
	    }

	    public boolean isBorderOpaque() {
	        return true;
	    }

	    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
	    }
	}
}
