package application;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.JButton;

public class AnalysisGame extends Game {
	private JButton stepForwardBtn;
	private JButton stepBackwardBtn;
	private JButton jumpToEndBtn;
	private JButton jumpToBeginningBtn;
	private File gameFile;

	public AnalysisGame(File gameFile) {
		super();
		this.gameFile = gameFile;
		buttonPanel.removeAll();
		stepForwardBtn = Main.getPlainLookbtn("Step->",
				"Open Sans", 28, Font.PLAIN, Color.YELLOW);
		stepBackwardBtn = Main.getPlainLookbtn("Step<-",
				"Open Sans", 28, Font.PLAIN, Color.RED);
		jumpToEndBtn = Main.getPlainLookbtn("EndGame",
				"Open Sans", 28, Font.PLAIN, Color.GREEN);
		jumpToBeginningBtn = Main.getPlainLookbtn("Beginning",
				"Open Sans", 28, Font.PLAIN, Color.CYAN);
		jumpToBeginningBtn.setMargin(Game.emptyMargin);
		jumpToEndBtn.setMargin(Game.emptyMargin);
		buttonPanel.add(stepForwardBtn);
		buttonPanel.add(stepBackwardBtn);
		buttonPanel.add(jumpToBeginningBtn);
		buttonPanel.add(jumpToEndBtn);
		addBtnListeners();
	}

	public void addBtnListeners() {

	}
}
