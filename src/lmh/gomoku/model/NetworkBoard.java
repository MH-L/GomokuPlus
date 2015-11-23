package lmh.gomoku.model;

import javax.swing.JPanel;

import lmh.gomoku.application.Game;
import lmh.gomoku.model.Board;

public final class NetworkBoard extends Board {


	public NetworkBoard(JPanel boardPanel, Game g) {
		super(boardPanel, g);
	}

	@Override
	public void addCellsToBoard(JPanel boardPanel) {}

	public void setSquare(int xcoord, int ycoord, Coordinate square) {
		// This is possibly changed from its correct version because
		// I could not find anything wrong before...
		grid[xcoord][ycoord] = square;
	}

	@Override
	public void resetSquare(int xcoord, int ycoord) {
		grid[xcoord][ycoord].setIcon(null);
	}

}
