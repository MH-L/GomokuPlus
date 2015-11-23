package lmh.gomoku.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import lmh.gomoku.application.Game;
import lmh.gomoku.application.Main;
import lmh.gomoku.exception.XMLException;
import lmh.gomoku.model.IMove;
import lmh.gomoku.util.RecordCreator;
import lmh.gomoku.util.XMLHelper;
import lmh.gomoku.util.XMLHelper.XMLElement;

public class AnalysisGame extends Game {
	private static final int ANIMATION_INTERVAL_DEFAULT = 1000;
	private Object booleanLock = new Object();
	private boolean animationAllowed = true;
	private JButton stepForwardBtn;
	private JButton stepBackwardBtn;
	private JButton jumpToEndBtn;
	private JButton jumpToBeginningBtn;
	private JButton startAnimationBtn;
	private JButton stopAnimationBtn;
	private JLabel statusLabel;
	private ArrayList<IMove> moves = new ArrayList<IMove>();
	private int curMoveIndex = 0;

	public AnalysisGame(File gameFile) {
		super();
		historyPanel.removeAll();
		buttonPanel.setLayout(new GridLayout(3, 2));
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
		startAnimationBtn = Main.getPlainLookbtn("StartAnimation",
				"Open Sans", 19, Font.PLAIN, Color.WHITE);
		stopAnimationBtn = Main.getPlainLookbtn("StopAnimation",
				"Open Sans", 19, Font.PLAIN, Color.gray);
		stepForwardBtn.setMargin(Game.emptyMargin);
		stepBackwardBtn.setMargin(Game.emptyMargin);
		jumpToBeginningBtn.setMargin(Game.emptyMargin);
		jumpToEndBtn.setMargin(Game.emptyMargin);
		startAnimationBtn.setMargin(Game.emptyMargin);
		stopAnimationBtn.setMargin(Game.emptyMargin);
		buttonPanel.add(stepForwardBtn);
		buttonPanel.add(stepBackwardBtn);
		buttonPanel.add(jumpToBeginningBtn);
		buttonPanel.add(jumpToEndBtn);
		buttonPanel.add(startAnimationBtn);
		buttonPanel.add(stopAnimationBtn);
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
				if (curMoveIndex > moves.size() - 1) {
					statusLabel.setText("<html>Already Last<br>Move!</html>");
					return;
				} else {
					renderOneMore();
					curMoveIndex ++;
					statusLabel.setText("Move " + curMoveIndex);
				}
			}
		});

		stepBackwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curMoveIndex <= 0) {
					statusLabel.setText("<html>Already First<br>Move!</html>");
					return;
				} else {
					renderOneLess();
					curMoveIndex --;
					statusLabel.setText("Move " + curMoveIndex);
				}
			}
		});

		jumpToBeginningBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderNone();
				curMoveIndex = 0;
				statusLabel.setText("Move " + curMoveIndex);
			}
		});

		jumpToEndBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMoveIndex = moves.size();
				renderAll();
				statusLabel.setText("Move " + curMoveIndex);
			}
		});

		addStopAndStartAnimationBtnListener();
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

	@Override
	protected void addAnimateMenuItemListener(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (booleanLock) {
					animationAllowed = true;
				}
				statusLabel.setText("Animating...");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (curMoveIndex >= moves.size() && animationAllowed) {
							timer.cancel();
							timer.purge();
							statusLabel.setText("Animation Finished.");
							return;
						}
						renderOneMore();
						curMoveIndex ++;
					}
				}, 2000, 2000);
			}
		});
	}

	@Override
	protected void addStepForwardMenuItemListener(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curMoveIndex > moves.size() - 1) {
					statusLabel.setText("<html>Already Last<br>Move!</html>");
					return;
				} else {
					renderOneMore();
					curMoveIndex ++;
					statusLabel.setText("Move " + curMoveIndex);
				}
			}
		});
	}

	@Override
	protected void addStepBackwardMenuItemListener(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curMoveIndex <= 0) {
					statusLabel.setText("<html>Already First<br>Move!</html>");
					return;
				} else {
					renderOneLess();
					curMoveIndex --;
					statusLabel.setText("Move " + curMoveIndex);
				}
			}
		});
	}

	@Override
	protected void addAnimationConfigurationMenuItemListener(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	private void addStopAndStartAnimationBtnListener() {
		stopAnimationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(booleanLock) {
					animationAllowed = false;
				}
			}
		});

		startAnimationBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (booleanLock) {
					animationAllowed = true;
				}
				statusLabel.setText("Animating...");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (curMoveIndex >= moves.size() || !animationAllowed) {
							timer.cancel();
							timer.purge();
							statusLabel.setText("Animation Finished.");
							return;
						}
						renderOneMore();
						curMoveIndex ++;
					}
				}, 0, ANIMATION_INTERVAL_DEFAULT);
			}
		});
	}

	private void renderAll() {
		for (int i = 0; i < moves.size(); i++) {
			IMove m = moves.get(i);
			int turn = (i % 2 == 0) ? TURN_SENTE : TURN_GOTE;
			this.board.setSquareByTurn(m.getX(), m.getY(), turn);
		}
	}

	private void renderNone() {
		board.resetBoard();
	}

	private void renderOneLess() {
		board.resetSquare(moves.get(curMoveIndex - 1).getX(),
				moves.get(curMoveIndex - 1).getY());

	}

	private void renderOneMore() {
		int turn = (curMoveIndex % 2 == 0) ? TURN_SENTE : TURN_GOTE;
		board.setSquareByTurn(moves.get(curMoveIndex).getX(),
				moves.get(curMoveIndex).getY(), turn);
	}
}
