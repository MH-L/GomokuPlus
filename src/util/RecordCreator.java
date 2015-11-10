package util;

import java.util.List;

import util.XMLHelper.XMLElement;
import model.ServerGame;
import model.ServerGame.Move;

public class RecordCreator {
	public static String generateRecordString(List<Move> moves, int result, int senteWithdrawals,
			int goteWithdrawals) {
		XMLHelper helper = new XMLHelper();
		XMLElement game = helper.new XMLElement("Game", null);
		XMLElement gameResult = helper.new XMLElement("Result", null);

		switch (result) {
		case ServerGame.RESULT_SENTE:
			gameResult.setContent("Sente");
			break;
		case ServerGame.RESULT_GOTE:
			gameResult.setContent("Gote");
			break;
		case ServerGame.RESULT_TIE:
			gameResult.setContent("Tie");
			break;
		}

		XMLElement withdrawals = helper.new XMLElement("Withdrawals", null);
		withdrawals.appendChild(helper.new XMLElement("Black", String.valueOf(senteWithdrawals)));
		withdrawals.appendChild(helper.new XMLElement("White", String.valueOf(goteWithdrawals)));
		game.appendChild(gameResult);
		game.appendChild(withdrawals);
		XMLElement steps = helper.new XMLElement("Moves", null);

		for (int i = 0; i < moves.size(); i += 2) {
			Move m = moves.get(i);
			XMLElement round = helper.new XMLElement("Round", null);
			XMLElement senteMove = helper.new XMLElement("Move", null);
			XMLElement senteMoveX = helper.new XMLElement("X", String.valueOf(m.getX()));
			XMLElement senteMoveY = helper.new XMLElement("Y", String.valueOf(m.getY()));
			senteMove.appendChild(senteMoveX);
			senteMove.appendChild(senteMoveY);
			round.appendChild(senteMove);

			if (i + 1 < moves.size()) {
				XMLElement goteMove = helper.new XMLElement("Move", null);
				XMLElement goteMoveX = helper.new XMLElement("X", String.valueOf(m.getX()));
				XMLElement goteMoveY = helper.new XMLElement("Y", String.valueOf(m.getY()));
				goteMove.appendChild(goteMoveX);
				goteMove.appendChild(goteMoveY);
				round.appendChild(goteMove);
			}
			steps.appendChild(round);
		}

		game.appendChild(steps);
		return XMLHelper.elementToString(game, 0);
	}
}
