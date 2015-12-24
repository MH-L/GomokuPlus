package lmh.gomoku.localStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lmh.gomoku.application.Main;
import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.StorageException;
import lmh.gomoku.exception.XMLException;
import lmh.gomoku.model.IMove;
import lmh.gomoku.util.RecordCreator;
import lmh.gomoku.util.XMLHelper;
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
			generateOptions();
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	/**
	 * Generates readme file of the game at the first time the game starts.
	 * @throws IOException
	 */
	public static void generateReadMe() throws IOException {
		File readme = new File(DIR + "\\README.txt");
		if (readme.createNewFile()) {
			PrintWriter writer = new PrintWriter(DIR + "\\README.txt", "UTF-8");
			String readmeContent =
					String.format("%s\n\n%s\n%s\n%s\n\n%s\n\n%s\n%s\n%s\n%s\n%s\n%s",
							"Welcome to Gomoku -- the simplest, yet one of the most interesting chess game.",
							"If you have never heard of gomoku, let me briefly explain the rules. The game is like",
							"an extended TicTacToe (which everyone knows), but the only difference is that, in order to",
							"win the game, you need to have five consecutive stones in a row, column or diagonal.",
							"Simple, huh? Yet it has many strategies, and you find it harder as you make progress.",
							"This directory is for game contents. \\config folder is where all game configs are",
							"stored, \\records is for game records which allow you to do retrospective studies",
							"of each game you played, and \\tokens is for login tokens of the game. Also do note",
							"that the animation interval and response interval in the config are in milliseconds,",
							"Do not attempt to modify these files because that may make them ill-formatted, thus",
							"cannot be used by the game.");
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
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private static void generateOptions()
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		File options = new File(CONFIG + "\\options.xml");
		XMLElement baseElement = new XMLElement("Options", null);
		XMLElement general = new XMLElement("General", null);
		XMLElement singlePlayer = new XMLElement("SingleplayerGame", null);
		XMLElement multiPlayer = new XMLElement("MultiplayerGame", null);
		XMLElement network = new XMLElement("NetworkGame", null);
		XMLElement AIGame = new XMLElement("AIGame", null);
		XMLElement analysisGame = new XMLElement("AnalysisGame", null);

		XMLElement boardWidth = new XMLElement("BoardWidth", "15");
		XMLElement enableTimed = new XMLElement("TimedGame", "Disabled");
		XMLElement timeLimit = new XMLElement("TimeLimit", "0");
		XMLElement responseTime = new XMLElement("ResponseInterval", "1000");
		XMLElement recordAutoSave = new XMLElement("RecordAutoSave", "Enabled");
		XMLElement animationInterval = new XMLElement("AnimationInterval", "1000");
		XMLElement backgroundColor = new XMLElement("BackgroundColor", "Default");
		XMLElement singlePlayerNumWithdrawal = new XMLElement("WithdrawalLimit", "4");
		XMLElement multiPlayerNumWithdrawal = new XMLElement("WithdrawalLimit", "2");
		XMLElement playerName = new XMLElement("PlayerName", "");

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
		if (options.createNewFile()) {
			PrintWriter writer = new PrintWriter(CONFIG + "\\options.xml", "UTF-8");
			String content = XMLHelper.elementToString(baseElement);
			writer.print(content);
			writer.close();
		}
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

	/**
	 * Stores the game record into local storage.
	 * @param moves Moves of a game
	 * @throws IOException
	 */
	public static void storeGameRecord(List<? extends IMove> moves) throws IOException {
		String moveStr = RecordCreator.generateRecordString(moves);
		String gameHash = ConnectionManager.getGameHash(System.currentTimeMillis(), 1, 2);
		gameHash = new String(Base64.getEncoder().encode(gameHash.getBytes()));
		gameHash = escapeBase64Str(gameHash);
		storeGameRecord(moveStr, gameHash);
	}
	
	/**
	 * Escapes the base 64 encoded string as file name format. "/" is 
	 * considered an invalid character in file names.
	 * @param str the base64 encoded string to escape
	 * @return string suitable for file names
	 */
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
	
	public Map<String, Object> getOptionsMapping() throws XMLException {
		File options = new File(CONFIG + "\\options.xml");
		// If options file does not exist then create one.
		if (!options.exists())
			try {
				generateOptions();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		String configString = "";
		
		try {
			FileInputStream fis = new FileInputStream(options);
			byte[] inputData = new byte[(int) options.length()];
			fis.read(inputData);
			configString = new String(inputData, "UTF-8");
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getOptionsMapping(configString);
	}
	
	private Map<String, Object> getOptionsMapping(String optionsString) throws XMLException {
		XMLElement optionsElement = null;
		optionsElement = XMLHelper.strToXML(optionsString);
		
		XMLElement general = tryAndGetChild(optionsElement, "General");
		XMLElement singlePlayerGame = tryAndGetChild(optionsElement, "SingleplayerGame");
		XMLElement multiplayerGame = tryAndGetChild(optionsElement, "MultiplayerGame");
		XMLElement networkGame = tryAndGetChild(optionsElement, "NetworkGame");
		XMLElement AIGame = tryAndGetChild(optionsElement, "AIGame");
		XMLElement analysisGame = tryAndGetChild(optionsElement, "AnalysisGame");
		
		Map<String, Object> generalsMap = new HashMap<String, Object>();
		Map<String, Object> singleplayerMap = new HashMap<String, Object>();
		Map<String, Object> multiplayerMap = new HashMap<String, Object>();
		Map<String, Object> aiGameMap = new HashMap<String, Object>();
		Map<String, Object> networkGameMap = new HashMap<String, Object>();
		Map<String, Object> analysisGameMap = new HashMap<String, Object>();
		
		return new HashMap<String, Object>();
	}
	
	private static XMLElement tryAndGetChild(XMLElement ele, String childName) throws XMLException {
		XMLElement retVal = ele.getFirstChild(childName);
		if (retVal == null)
			throw new XMLException("Child not found.");
		
		return retVal;
	}
}
