package lmh.gomoku.model;

import javax.swing.JButton;

public class Coordinate extends JButton {
	private static final long serialVersionUID = -581532617710492838L;
	public int x;
	public int y;
	public Stone stone;

	public enum Stone {
		UNOCCUPIED, FIRST, SECOND
	}

	public Coordinate(int y, int x) {
		this.y = y;
		this.x = x;
		stone = Stone.UNOCCUPIED;
	}

	public boolean isUnoccupied() {
		return stone == Stone.UNOCCUPIED;
	}

	public void setStone(boolean isFirst) {
		stone = isFirst ? Stone.FIRST : Stone.SECOND;
	}

	public Stone getStone() {
		return stone;
	}

	public int getXCoord() {
		return x;
	}

	public int getYCoord() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public void reset() {
		this.stone = Stone.UNOCCUPIED;
		this.setIcon(null);
	}
}
