package Model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import application.NetworkGame;
import database.ConnectionManager;

public final class NetworkBoard extends Board {


	public NetworkBoard(JPanel boardPanel) {
		super(boardPanel);
	}

	@Override
	public void addCellsToBoard(JPanel boardPanel) {}

	public void setSquare(int xcoord, int ycoord, Coordinate square) {
		// This is possibly changed from its correct version because
		// I could not find anything wrong before...
		grid[xcoord][ycoord] = square;
	}

	public void resetSquare(int xcoord, int ycoord) {
		grid[xcoord][ycoord].setIcon(null);
	}

}
