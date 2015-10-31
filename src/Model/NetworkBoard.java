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

public final class NetworkBoard extends Board {


	public NetworkBoard(JPanel boardPanel) {
		super(boardPanel);

	}

	@Override
	public void addCellsToBoard(JPanel boardPanel) {}

	public void setSquare(int xcoord, int ycoord, Coordinate square) {
		grid[xcoord][ycoord] = square;
	}

}
