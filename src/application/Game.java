package application;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game {
	JPanel boardPanel;
	JFrame mainFrame;
	JButton btnStart;
	JButton btnGiveUp;

	public Game() {
		boardPanel = new JPanel();
		mainFrame = new JFrame("Gomoku Plus");
		mainFrame.setSize(1000, 600);
		btnStart = new JButton("Start!");
		btnStart.setBackground(Color.CYAN);
		btnStart.setFont(new Font("Open Sans", Font.PLAIN, 28));
		btnGiveUp = new JButton("Give UP!");
		btnGiveUp.setBackground(Color.RED);
		btnGiveUp.setFont(new Font("Open Sans", Font.PLAIN, 28));
		mainFrame.add(boardPanel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		boardPanel.add(btnStart);
		boardPanel.add(btnGiveUp);
	}
}
