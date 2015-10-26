package Model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

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
				JButton square = new Coordinate(j, i);
				square.setBackground(Color.YELLOW);
				square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				square.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (activePlayer == 1) {

						} else {

						}
						// Also need to update relevant information. This could be a hard task.
					}
				});
				boardPanel.add(square);
				grid[j][i] = (Coordinate) square;
			}
		}
	}

	public boolean isGameOver() {
		return false;
	}
}
