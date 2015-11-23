package lmh.gomoku.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import lmh.gomoku.application.Game;
import lmh.gomoku.application.SingleplayerGame;
import lmh.gomoku.application.Game.Result;
import lmh.gomoku.model.Board;
import lmh.gomoku.model.Coordinate.Stone;

public class Board {
	protected static Coordinate[][] grid;
	public static final int width = 15;
	public static final int height = 15;
	private int activePlayer;
	private boolean isFrozen = true;
	private Coordinate lastMove1 = null;
	private Coordinate lastMove2 = null;
	private int stoneCount = 0;
	private Game g;
	private boolean suspensionRequired = false;

	public Board(JPanel boardPanel, Game g) {
		this.g = g;
		grid = new Coordinate[height][width];
		activePlayer = 1;
		addCellsToBoard(boardPanel);
	}

	public Board(JPanel boardPanel, Game g, boolean suspensionRequired) {
		this(boardPanel, g);
		this.suspensionRequired = suspensionRequired;
	}

	protected void addCellsToBoard(JPanel boardPanel) {
		// TODO implement the case where suspension is required (for
		// single player game exclusively).
		boardPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		for (int i = 0; i < Board.height; i++) {
			for (int j = 0; j < Board.width; j++) {
				Coordinate square = new Coordinate(i, j);
				square.setBackground(Game.boardColor);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isFrozen) {
							g.warnGameFrozen();
							return;
						}
						if (isBoardFull()) {
							g.displayTieMessage();
							g.gameEnd();
							return;
						}
						if (square.isUnoccupied()) {
							if (suspensionRequired) {
								if (!((SingleplayerGame) g).playerCanMove(activePlayer)) {
									g.warnNotYourTurn();
									return;
								}
							}
							if (activePlayer == 1) {
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occupied.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									g.errorRendering();
								}
								square.setStone(true);
								updateActivePlayer();
							} else {
								try {
									Image img = ImageIO.read(getClass().getResource("/images/occ.png"));
									square.setIcon(new ImageIcon(img));
								} catch (IOException e1) {
									g.errorRendering();
								}
								square.setStone(false);
								updateActivePlayer();
							}
							lastMove2 = lastMove1;
							lastMove1 = square;
						} else {
							g.displayOccupiedWarning();
						}
						Result currentGameResult = checkWinning();
						if (currentGameResult != Result.UNDECIDED) {
							isFrozen = true;
							if (currentGameResult == Result.SENTE) {
								g.displayWinnerInfo(true);
							} else {
								g.displayWinnerInfo(false);
							}
							g.gameEnd();
						}
					}
				});
				boardPanel.add(square);
				grid[i][j] = square;
			}
		}
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
					if (grid[i][j].getStone() == prev) {
						counter ++;
					} else {
						counter = 1;
					}
				} else {
					counter = 0;
				}
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[i][j].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else {
						return Result.GOTE;
					}
				}
				prev = grid[i][j].getStone();
			}
		}

		// Check for columns.
		for (int i = 0; i < width; i++) {
			int counter = 0;
			Stone prev = Stone.UNOCCUPIED;
			for (int j = 0; j < height; j++) {
				if (grid[j][i].getStone() != Stone.UNOCCUPIED) {
					if (grid[j][i].getStone() == prev) {
						counter ++;
					} else {
						counter = 1;
					}
				} else {
					counter = 0;
				}
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[j][i].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else {
						return Result.GOTE;
					}
				}
				prev = grid[j][i].getStone();
			}
		}
		return Result.UNDECIDED;
	}

	/**
	 * If you were to maintain this code, keep in mind that it has
	 * no readability AT ALL. The code itself is smart, yet extremely
	 * hard to read. (Oh, this passed on my first attempt.)
	 * @return
	 */
	private Result checkDiagWinning() {
		int i = 0;
		int j = 0;
		int iStartIndex = 0;
		int jStartIndex = 0;
		while (i + j < width + height - 1) {
			int counter = 0;
			Stone prev = Stone.UNOCCUPIED;
			while (j > -1 && i < height) {
				if (grid[i][j].getStone() != Stone.UNOCCUPIED) {
					if (grid[i][j].getStone() == prev) {
						counter ++;
					} else {
						counter = 1;
					}
				} else
					counter = 0;
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[i][j].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else {
						return Result.GOTE;
					}
				}
				prev = grid[i][j].getStone();
				i++;
				j--;
			}
			if (jStartIndex >= width - 1) {
				iStartIndex ++;
			} else {
				jStartIndex ++;
			}
			i = iStartIndex;
			j = jStartIndex;
		}

		j = width - 1;
		i = 0;
		iStartIndex = 0;
		jStartIndex = width - 1;
		while (i - j < width) {
			int counter = 0;
			Stone prev = Stone.UNOCCUPIED;
			while (j < width && i < height) {
				if (grid[i][j].getStone() != Stone.UNOCCUPIED) {
					if (grid[i][j].getStone() == prev) {
						counter ++;
					} else {
						counter = 1;
					}
				} else {
					counter = 0;
				}
				if (counter == Game.NUM_STONE_TO_WIN) {
					if (grid[i][j].getStone() == Stone.FIRST) {
						return Result.SENTE;
					} else {
						return Result.GOTE;
					}
				}
				prev = grid[i][j].getStone();
				i++;
				j++;
			}
			if (jStartIndex > 0) {
				jStartIndex --;
			} else {
				iStartIndex ++;
			}
			i = iStartIndex;
			j = jStartIndex;
		}
		return Result.UNDECIDED;
	}

	public Result checkWinning() {
		Result rowsAndCols = checkRowColWinning();
		if (rowsAndCols != Result.UNDECIDED) {
			return rowsAndCols;
		}
		return checkDiagWinning();
	}

	public int getActivePlayer() {
		return activePlayer;
	}

	public void resetBoard() {
		activePlayer = 1;
		for (Coordinate[] coords : grid) {
			for (Coordinate coord : coords) {
				coord.reset();
			}
		}
	}

	public void freeze() {
		isFrozen = true;
	}

	public void activate() {
		isFrozen = false;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public boolean withdraw() {
		if (lastMove1 != null && lastMove2 != null) {
			grid[lastMove1.getYCoord()][lastMove1.getXCoord()].reset();
			lastMove1 = null;
			grid[lastMove2.getYCoord()][lastMove2.getXCoord()].reset();
			lastMove2 = null;
			return true;
		}
		return false;
	}

	public boolean isBoardFull() {
		return stoneCount >= width * height;
	}

	public static Coordinate findEmptyLocSpiral() {
		int curX = width / 2;
		int curY = height / 2;
		int curIncX = 1;
		int curIncY = 0;

		while (Board.isReachable(curX, curY)) {
			if (grid[curY][curX].isUnoccupied()) {
				return grid[curY][curX];
			}
			if (curX == curY && curX < width / 2) {
				curIncX = 0;
				curIncY = 1;
			} else if (curX + curY == width - 1 && curX < width / 2) {
				curIncX = 1;
				curIncY = 0;
			} else if (curX == curY + 1 && curX > width / 2) {
				curIncX = 0;
				curIncY = -1;
			} else if (curX + curY == width - 1 && curX > width / 2) {
				curIncX = -1;
				curIncY = 0;
			}
			curY += curIncY;
			curX += curIncX;
		}

		return null;
	}

	private static boolean isReachable(int x, int y) {
		return x < width && y < height && x >= 0 && y >= 0;
	}

	public void setSquareByTurn(int x, int y, int turn) {
		if (turn == Game.TURN_SENTE) {
			try {
				Image img = ImageIO.read(getClass().getResource("/images/occupied.png"));
				grid[y][x].setIcon(new ImageIcon(img));
			} catch (IOException e1) {
				g.errorRendering();
			}
		} else if (turn == Game.TURN_GOTE) {
			try {
				Image img = ImageIO.read(getClass().getResource("/images/occ.png"));
				grid[y][x].setIcon(new ImageIcon(img));
			} catch (IOException e1) {
				g.errorRendering();
			}
		} else {
			// Something went wrong apparently.
			grid[y][x].setBackground(Color.RED);
		}
	}

	public void resetSquare(int x, int y) {
		grid[y][x].reset();
	}
}