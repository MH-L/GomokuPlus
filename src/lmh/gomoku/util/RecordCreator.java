package lmh.gomoku.util;

import java.util.ArrayList;
import java.util.List;

import lmh.gomoku.application.NetworkGame.PortableMove;
import lmh.gomoku.exception.XMLException;
import lmh.gomoku.model.IMove;
import lmh.gomoku.model.ServerGame;
import lmh.gomoku.util.XMLHelper.XMLElement;

public class RecordCreator {
	public static final String RECORD_FILE_TYPE_SUFFIX = ".xml";
	public static String generateRecordString(List<? extends IMove> moves, int result, int senteWithdrawals,
			int goteWithdrawals) {
		XMLHelper helper = new XMLHelper();
		XMLElement game = new XMLElement("Game", null);
		XMLElement gameResult = new XMLElement("Result", null);

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
		default:
			gameResult.setContent("Not Applicable");
		}

		XMLElement withdrawals = new XMLElement("Withdrawals", null);
		if (senteWithdrawals >= 0) {
			withdrawals.appendChild(new XMLElement("Black", String.valueOf(senteWithdrawals)));
		} else {
			withdrawals.appendChild(new XMLElement("Black", "Not Applicable"));
		}

		if (goteWithdrawals >= 0) {
			withdrawals.appendChild(new XMLElement("White", String.valueOf(goteWithdrawals)));
		} else {
			withdrawals.appendChild(new XMLElement("White", "Not Applicable"));
		}
		game.appendChild(gameResult);
		game.appendChild(withdrawals);
		XMLElement steps = new XMLElement("Moves", null);

		for (int i = 0; i < moves.size(); i += 2) {
			IMove m = moves.get(i);
			XMLElement round = new XMLElement("Round", null);
			XMLElement senteMove = new XMLElement("Move", null);
			XMLElement senteMoveX = new XMLElement("X", String.valueOf(m.getX()));
			XMLElement senteMoveY = new XMLElement("Y", String.valueOf(m.getY()));
			senteMove.appendChild(senteMoveX);
			senteMove.appendChild(senteMoveY);
			round.appendChild(senteMove);

			if (i + 1 < moves.size()) {
				m = moves.get(i + 1);
				XMLElement goteMove = new XMLElement("Move", null);
				XMLElement goteMoveX = new XMLElement("X", String.valueOf(m.getX()));
				XMLElement goteMoveY = new XMLElement("Y", String.valueOf(m.getY()));
				goteMove.appendChild(goteMoveX);
				goteMove.appendChild(goteMoveY);
				round.appendChild(goteMove);
			}
			steps.appendChild(round);
		}

		game.appendChild(steps);
		return XMLHelper.elementToString(game);
	}

	public static String generateRecordString(List<? extends IMove> moves) {
		return generateRecordString(moves, 0, -1, -1);
	}

	public static ArrayList<IMove> generateMovesFromXML(XMLElement gameXML) throws XMLException {
		ArrayList<IMove> retVal = new ArrayList<IMove>();
		List<XMLElement> moves = gameXML.getChild("Moves");
		if (!(moves.size() == 1)) {
			throw new XMLException("Game XML does not contain moves.");
		}

		List<XMLElement> rounds = moves.get(0).getChild("Round");
		if (rounds.isEmpty()) {
			throw new XMLException("No rounds in move XML element.");
		}

		for (int i = 0; i < rounds.size(); i++) {
			List<XMLElement> movesInRound = rounds.get(i).getChild("Move");
			if (movesInRound.isEmpty()) {
				throw new XMLException("No moves in round.");
			} else if (movesInRound.size() > 2) {
				throw new XMLException("Move than two moves in round.");
			} else if (movesInRound.size() == 1) {
				if (i == rounds.size() - 1) {
					XMLElement onlyMove = movesInRound.get(0);
					List<XMLElement> Xs = onlyMove.getChild("X");
					List<XMLElement> Ys = onlyMove.getChild("Y");
					if (Xs.size() != 1 || Ys.size() != 1) {
						throw new XMLException("Incorrect X or Y coordinate in move.");
					}
					try {
						int xcoord = Integer.parseInt(Xs.get(0).getContent());
						int ycoord = Integer.parseInt(Ys.get(0).getContent());
						IMove mv = new PortableMove(xcoord, ycoord);
						retVal.add(mv);
					} catch (Exception e) {
						throw new XMLException("X-coordinates and Y-coordinates should be numbers.");
					}
				} else {
					throw new XMLException("Only one move in non-last round.");
				}
			} else {
				for (int j = 0; j < movesInRound.size(); j++) {
					XMLElement onlyMove = movesInRound.get(j);
					List<XMLElement> Xs = onlyMove.getChild("X");
					List<XMLElement> Ys = onlyMove.getChild("Y");
					if (Xs.size() != 1 || Ys.size() != 1) {
						throw new XMLException("Incorrect X or Y coordinate in move.");
					}
					try {
						int xcoord = Integer.parseInt(Xs.get(0).getContent());
						int ycoord = Integer.parseInt(Ys.get(0).getContent());
						IMove mv = new PortableMove(xcoord, ycoord);
						retVal.add(mv);
					} catch (Exception e) {
						throw new XMLException("X-coordinates and Y-coordinates should be numbers.");
					}
				}
			}
		}

		return retVal;
	}
}
