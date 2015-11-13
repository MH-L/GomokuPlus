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
import util.RecordCreator;
import util.XMLHelper;
import util.XMLHelper.XMLElement;
import model.IMove;

public class AnalysisGame extends Game {
	private JButton stepForwardBtn;
	private JButton stepBackwardBtn;
	private JButton jumpToEndBtn;
	private JButton jumpToBeginningBtn;
	private JLabel statusLabel;
	private ArrayList<IMove> moves = new ArrayList<IMove>();
	private int curMoveIndex = 0;

	public AnalysisGame(File gameFile) {
		super();
		historyPanel.removeAll();
		statusLabel = new JLabel();
		statusLabel.setFont(Game.mediumGameFont);
		historyPanel.add(statusLabel);
		JLabel analysisGameLabel = new JLabel("Analysis Mode");
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
		try {
			getAllMoves(gameFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(mainFrame, "Cannot process input file.",
					"Error Processing File", JOptionPane.WARNING_MESSAGE);
			statusLabel.setText("File Invalid");
		} catch (XMLException e) {
			JOptionPane.showMessageDialog(mainFrame, e.getMessage(),
					"Malformed XML", JOptionPane.WARNING_MESSAGE);
			statusLabel.setText("File Invalid");
		}
		addBtnListeners();
		statusLabel.setText("Ready");
	}

	public void addBtnListeners() {
		stepForwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMoveIndex = 0;
			}
		});

		stepBackwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curMoveIndex <= 0) {
					statusLabel.setText("Already First Move!");
					return;
				} else {
					curMoveIndex --;
				}
			}
		});

		jumpToBeginningBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curMoveIndex >= moves.size() - 1) {
					statusLabel.setText("Already Last Move!");
					return;
				} else {
					curMoveIndex ++;
				}
			}
		});

		jumpToEndBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMoveIndex = moves.size() - 1;
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
		moves = RecordCreator.generateMovesFromXML(gameElement);
	}

	private void renderAll() {

	}

	private void renderNone() {

	}

	private void renderOneLess() {

	}

	private void renderOneMove() {

	}
}
