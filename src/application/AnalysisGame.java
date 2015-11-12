package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import exceptions.XMLException;
import util.XMLHelper;
import util.XMLHelper.XMLElement;
import model.IMove;

public class AnalysisGame extends Game {
	private JButton stepForwardBtn;
	private JButton stepBackwardBtn;
	private JButton jumpToEndBtn;
	private JButton jumpToBeginningBtn;
	private ArrayList<IMove> moves;
	private int curMoveIndex;

	public AnalysisGame(File gameFile) {
		super();
		JLabel analysisGameLabel = new JLabel("Analysis Game");
		analysisGameLabel.setFont(Game.largeGameFont);
		titlePanel.setPreferredSize(new Dimension(Game.functionPanelWidth, 150));
		titlePanel.add(analysisGameLabel);
		JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		titleSeparator.setPreferredSize(new Dimension(200, 3));
		titlePanel.add(titleSeparator);
		buttonPanel.removeAll();
		stepForwardBtn = Main.getPlainLookbtn("StepForward",
				"Open Sans", 22, Font.PLAIN, Color.YELLOW);
		stepBackwardBtn = Main.getPlainLookbtn("StepBack",
				"Open Sans", 28, Font.PLAIN, Color.RED);
		jumpToEndBtn = Main.getPlainLookbtn("EndGame",
				"Open Sans", 28, Font.PLAIN, Color.GREEN);
		jumpToBeginningBtn = Main.getPlainLookbtn("Beginning",
				"Open Sans", 28, Font.PLAIN, Color.CYAN);
		stepForwardBtn.setMargin(Game.emptyMargin);
		stepBackwardBtn.setMargin(Game.emptyMargin);
		jumpToBeginningBtn.setMargin(Game.emptyMargin);
		jumpToEndBtn.setMargin(Game.emptyMargin);
		buttonPanel.add(stepForwardBtn);
		buttonPanel.add(stepBackwardBtn);
		buttonPanel.add(jumpToBeginningBtn);
		buttonPanel.add(jumpToEndBtn);
		addBtnListeners();
		try {
			getAllMoves(gameFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(mainFrame, "Cannot process input file.",
					"Error Processing File", JOptionPane.WARNING_MESSAGE);
		} catch (XMLException e) {
			JOptionPane.showMessageDialog(mainFrame, e.getMessage(),
					"Malformed XML", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void addBtnListeners() {
		stepForwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		stepBackwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		jumpToBeginningBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		jumpToEndBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	public void getAllMoves(File record) throws IOException, XMLException {
		FileInputStream fls = new FileInputStream(record);
		byte[] data = new byte[(int) record.length()];
		fls.read(data);
		fls.close();
		String allContents = new String(data, "UTF-8");
		XMLElement gameElement = XMLHelper.strToXML(allContents);
	}
}
