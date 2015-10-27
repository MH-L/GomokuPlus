package Model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import Model.Coordinate.Stone;
import application.Game;
import application.Game.Result;

public class Board {
	private Coordinate[][] grid;
	public static final int width = 15;
	public static final int height = 15;
	public static int activePlayer;

	public Board(JPanel boardPanel) {
		this.grid = new Coordinate[height][width];
		activePlayer = 1;
		addCellsToBoard(boardPanel);
	}

	private void addCellsToBoard(JPanel boardPanel) {
		boardPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		for (int i = 0; i < Board.height; i++) {
			for (int j = 0; j < Board.width; j++) {
				Coordinate square = new Coordinate(j, i);
				square.setBackground(Color.YELLOW);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (square.isUnoccupied()) {
							if (activePlayer == 1) {
								square.setBackground(Color.RED);
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occ.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									Game.errorRendering();
								}
								square.setStone(true);
								updateActivePlayer();
							} else {
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occupied.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									Game.errorRendering();
								}
								square.setStone(false);
								updateActivePlayer();
							}
						} else {
							Game.displayOccupiedWarning();
						}
						Result currentGameResult = checkWinning();
						if (currentGameResult != Result.UNDECIDED) {
							if (currentGameResult == Result.SENTE) {

							} else {

							}
						}
					}
				});
				boardPanel.add(square);
				grid[j][i] = square;
			}
		}
	}

	public boolean isGameOver() {
		return false;
	}

	public void updateActivePlayer() {
		activePlayer = activePlayer == 1 ? 2 : 1;
	}

	private Result checkRowColWinning() {
		// Check for rows.
		for (int i = 0; i < height; i++) {
			int counter = 0;
			Stone prev = Stone.UNOCCUPIED;
			for (int j = 0; j < width; j++) {
				if (grid[i][j].getStone() != Stone.UNOCCUPIED) {
					if (grid[i][j].getStone() == prev)
						counter ++;
					else
						counter = 0;
				} else
					counter = 0;
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[i][j].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else
						return Result.GOTE;
				}
			}
		}

		// Check for columns.
		for (int i = 0; i < width; i++) {
			int counter = 0;
			Stone prev = Stone.UNOCCUPIED;
			for (int j = 0; j < height; j++) {
				if (grid[j][i].getStone() != Stone.UNOCCUPIED) {
					if (grid[j][i].getStone() == prev)
						counter ++;
					else
						counter = 0;
				} else
					counter = 0;
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[j][i].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else
						return Result.GOTE;
				}
			}
		}
		return Result.UNDECIDED;
	}

	private Result checkDiagWinning() {
		return Result.UNDECIDED;
	}

	public Result checkWinning() {
		Result rowsAndCols = checkRowColWinning();
		if (rowsAndCols != Result.UNDECIDED)
			return rowsAndCols;
		return checkDiagWinning();
	}
}
