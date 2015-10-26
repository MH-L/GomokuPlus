package application;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

public class NetworkGame extends Game {
	private JButton btnProposeTie;
	private JButton btnTryWithdraw;
	public NetworkGame() {
		super();
		btnProposeTie = Main.getPlainLookbtn("Propose Tie!",
				"Open Sans", 28, Font.ITALIC, Color.GREEN);
		this.mainPanel.add(btnProposeTie);
		btnTryWithdraw = Main.getPlainLookbtn("Try Withdraw",
				"Open Sans", 28, Font.PLAIN, Color.YELLOW);
		this.mainPanel.add(btnTryWithdraw);
	}
}
