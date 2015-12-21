package lmh.gomoku.application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;

import renju.com.lmh.application.Game.Difficulty;
import renju.com.lmh.exception.InvalidIndexException;
import renju.com.lmh.model.BoardLocation;
import lmh.gomoku.application.Game;
import lmh.gomoku.application.Main;
import lmh.gomoku.model.Board;
import lmh.gomoku.model.Coordinate;
import lmh.gomoku.model.ServerGame.Move;

public class SingleplayerGame extends Game {
	/**
	 * Specifies the maximum number of withdrawals a player can get. Player gets
	 * higher score if he/she uses less withdrawals.
	 */
	private static final int MAX_NUM_WITHDRAWAL = 5;
	private static int withdrawalLeft;
	private JButton btnWithdrawal;
	private JButton btnHint;
	private GameEngine engine;
	private HumanPlayer player;
	private renju.com.lmh.model.Board analysisBoard;

	public SingleplayerGame(int max_num_withdrawal, int playerTurn) {
		super(true);
		this.analysisBoard = new renju.com.lmh.model.Board(Board.width);
		this.player = new HumanPlayer(playerTurn);
		this.engine = new GameEngine(Difficulty.INTERMEDIATE, analysisBoard,
				playerTurn == Game.TURN_GOTE);
		withdrawalLeft = max_num_withdrawal;
		this.btnWithdrawal = Main.getPlainLookbtn("Withdraw!", "Open Sans", 23,
				Font.PLAIN, Color.GRAY);
		btnWithdrawal.setMargin(new Insets(0, 0, 0, 0));
		this.btnWithdrawal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				withdraw();
			}
		});
		this.btnHint = Main.getPlainLookbtn("Hint", "Open Sans", 23,
				Font.PLAIN, Color.PINK);
		btnHint.setMargin(new Insets(0, 0, 0, 0));
		btnHint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (board.isFrozen() || board.isBoardFull()) {
					return;
				}
				Coordinate cor = Board.findEmptyLocSpiral();
				Timer flashSet = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						if (cor.getBackground().equals(Color.YELLOW)) {
							cor.setBackground(Color.GREEN);
						} else {
							cor.setBackground(Color.YELLOW);
							((Timer) event.getSource()).stop();
						}
					}
				});
				flashSet.setInitialDelay(0);
				flashSet.start();
			}
		});
		buttonPanel.add(btnWithdrawal);
		buttonPanel.add(btnHint);
		JLabel titleLabel = new JLabel("<html>Singleplayer<br>Game</html>");
		titleLabel.setFont(new Font("Open Sans", Font.PLAIN, 40));
		titlePanel.add(titleLabel);
	}

	@Override
	public void withdraw() {
		if (withdrawalLeft <= 0) {
			displayWithdrawFailed();
			return;
		}
		if (board.withdraw()) {
			withdrawalLeft--;
		} else {
			displayWithdrawFailed();
		}
	}

	public BoardLocation AIMakeMove() {
		try {
			BoardLocation aiMove = engine.makeMove();
			updateBoardForAI(aiMove.getXPos(), aiMove.getYPos(), false);
			System.out.println("The Board has num of stones: " + this.engine.getSolver().getBoard().getTotalStones());
			return aiMove;
		} catch (InvalidIndexException e) {
			return null;
		}
	}

	public boolean playerCanMove(int activePlayer) {
		 return player.getTurn() == activePlayer;
	}

	public boolean updateBoardForAI(int xcoord, int ycoord, boolean isForPlayer) {
		try {
			this.engine.getSolver().getBoard().updateBoard(new BoardLocation(ycoord, xcoord),
					isForPlayer ? (player.getTurn() == Game.TURN_SENTE) :
						(player.getTurn() == Game.TURN_GOTE));
		} catch (InvalidIndexException e) {
			return false;
		}
		return true;
	}

	@Override
	public void gameEnd() {
		super.gameEnd();
		engine.endGameCleanup();
	}
}
