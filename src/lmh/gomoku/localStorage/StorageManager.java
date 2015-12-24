package lmh.gomoku.localStorage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.StorageException;
import lmh.gomoku.model.IMove;
import lmh.gomoku.util.RecordCreator;
import lmh.gomoku.util.XMLHelper.XMLElement;

/**
 * Class for local game storage. This class is not for server.
 * @author Minghao
 *
 */
public class StorageManager {
	/**
	 * The game's root directory.
	 */
	private static final String DIR = System.getProperty("user.home") + "\\Gomoku Plus";
	/**
	 * Directory for game records.
	 */
	private static final String RECORD = DIR + "\\records";
	/**
	 * Directory for login tokens.
	 */
	private static final String TOKEN = DIR + "\\tokens";
	/**
	 * Directory for configuration values.
	 */
	private static final String CONFIG = DIR + "\\config";

	public static void initializeStorage() throws StorageException {
		File gameMainDir = new File(DIR);
		if (gameMainDir.exists()) {
			File gameRecordsDir = new File(RECORD);
			File gameTokensDir = new File(TOKEN);
			File gameConfigsDir = new File(CONFIG);
			if (!gameRecordsDir.exists()) {
				boolean success = gameRecordsDir.mkdir();
				if (!success) {
					throw new StorageException("Unable to initialize game storage -- records folder.");
				}
			}

			if (!gameTokensDir.exists()) {
				boolean success = gameTokensDir.mkdir();
				if (!success) {
					throw new StorageException("Unable to initialize game storage -- tokens folder.");
				}
			}

			if (!gameConfigsDir.exists()) {
				boolean success = gameConfigsDir.mkdir();
				if (!success) {
					throw new StorageException("Unable to initialize game storage -- config folder.");
				}
			}
		} else {
			boolean success = gameMainDir.mkdir();
			if (!success) {
				throw new StorageException("Unable to initialize game storage -- main folder.");
			}
			File gameRecordsDir = new File(RECORD);
			File gameTokensDir = new File(TOKEN);
			boolean mkdirSuccess = gameRecordsDir.mkdir() && gameTokensDir.mkdir();
			if (!mkdirSuccess) {
				throw new StorageException("Unable to initialize game storage -- subFolder.");
			}
		}
		try {
			generateReadMe();
		} catch (IOException e) {
			throw new StorageException();
		}
	}



	public static void generateReadMe() throws IOException {
		File readme = new File(DIR + "\\README.txt");
		if (readme.createNewFile()) {
			PrintWriter writer = new PrintWriter(DIR + "\\README.txt", "UTF-8");
			String readmeContent =
					String.format("%s\n\n%s\n%s\n%s\n\n%s\n\n%s\n%s\n%s",
							"Welcome to Gomoku -- the simplest, yet one of the most interesting chess game.",
							"If you have never heard of gomoku, let me briefly explain the rules. The game is like",
							"an extended TicTacToe (which everyone knows), but the only difference is that, in order to",
							"win the game, you need to have five consecutive stones in a row, column or diagonal.",
							"Simple, huh? Yet it has many strategies, and you find it harder as you make progress.",
							"This directory is for game contents. \\config folder is where all game configs are",
							"stored, \\records is for game records which allow you to do retrospective studies",
							"of each game you played, and \\tokens is for login tokens of the game.");
			writer.print(readmeContent);
			writer.close();
		}
	}

	public static void storeToken(byte[] token) {

	}

	/**
	 * Generates the option file for the game when the game starts for the first time.
	 * <Tentative plan>
	 * Options file will include:
	 * 1. Board Size (15 - 30)??
	 * 2. Board Background color (with a few options)
	 * 3. Auto save game records
	 * 4. Animation interval for AI game
	 * 5. Animation interval for analysis game
	 * 6. Default AI difficulty for AI game
	 * 7. Enable/disable timed singleplayer game and time limit
	 * 8. Number of withdrawals given to player
	 * 9. Player name in network game
	 */
	private static void generateOptions() {
		File options = new File(CONFIG + "\\options.xml");
		XMLElement baseElement = new XMLElement("Options", null);
		XMLElement general = new XMLElement("General", null);
		XMLElement singlePlayer = new XMLElement("Singleplayer Game", null);
		XMLElement multiPlayer = new XMLElement("Multiplayer Game", null);
		XMLElement network = new XMLElement("Network Game", null);
		XMLElement AIGame = new XMLElement("AI Game", null);
		XMLElement analysisGame = new XMLElement("Analysis Game", null);

		XMLElement boardWidth = new XMLElement("Board Width", "15");
		XMLElement enableTimed = new XMLElement("Timed Game", "Disabled");
		XMLElement timeLimit = new XMLElement("Time Limit", "0");
		XMLElement responseTime = new XMLElement("Response Interval", "1000");
		XMLElement recordAutoSave = new XMLElement("Record Auto Save", "Enabled");
		XMLElement animationInterval = new XMLElement("Animation Interval", "1000");
		XMLElement backgroundColor = new XMLElement("Background Color", "Default");
		XMLElement singlePlayerNumWithdrawal = new XMLElement("Withdrawal Limit", "4");
		XMLElement multiPlayerNumWithdrawal = new XMLElement("Withdrawal Limit", "2");
		XMLElement playerName = new XMLElement("Player Name", "");

		baseElement.appendChild(general);
		baseElement.appendChild(singlePlayer);
		baseElement.appendChild(multiPlayer);
		baseElement.appendChild(network);
		baseElement.appendChild(AIGame);
		baseElement.appendChild(analysisGame);
		general.appendChild(boardWidth);
		general.appendChild(backgroundColor);
		general.appendChild(recordAutoSave);
		singlePlayer.appendChild(enableTimed);
		singlePlayer.appendChild(timeLimit);
		singlePlayer.appendChild(singlePlayerNumWithdrawal);
		multiPlayer.appendChild(multiPlayerNumWithdrawal);
		network.appendChild(playerName);
		AIGame.appendChild(responseTime);
		analysisGame.appendChild(animationInterval);

	}

	private static void storeGameRecord(String record, String gameHash) throws IOException {
		String fileNameStr = String.format("%s%s%s%s", RECORD, "\\", gameHash,
				RecordCreator.RECORD_FILE_TYPE_SUFFIX);
		System.out.println(fileNameStr);
		File f = new File(fileNameStr);
		f.createNewFile();
		PrintWriter writer = new PrintWriter(fileNameStr, "UTF-8");
		writer.print(record);
		writer.close();
	}

	public static void storeGameRecord(List<? extends IMove> moves) throws IOException {
		String moveStr = RecordCreator.generateRecordString(moves);
		String gameHash = ConnectionManager.getGameHash(System.currentTimeMillis(), 1, 2);
		gameHash = new String(Base64.getEncoder().encode(gameHash.getBytes()));
		gameHash = escapeBase64Str(gameHash);
		storeGameRecord(moveStr, gameHash);
	}

	private static String escapeBase64Str(String str) {
		char[] arr = str.toCharArray();
		String retVal = "";
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == '/') {
				retVal += '_';
			} else {
				retVal += arr[i];
			}
		}

		return retVal;
	}
}
