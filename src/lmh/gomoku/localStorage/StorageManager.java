package lmh.gomoku.localStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lmh.gomoku.application.Game;
import lmh.gomoku.application.Game.Result;
import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.StorageException;
import lmh.gomoku.model.Board;
import lmh.gomoku.application.Main;
import lmh.gomoku.application.Options;
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
	private static int winNum=0;
	private static int loseNum=0;
	private static int tie=0;
	private static float percentage;

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
			generateInitialStatsFile();
			

		} catch (IOException e) {
			throw new StorageException();
		}
	}

   	
	private static void generateInitialStatsFile() throws StorageException {
		// TODO Auto-generated method stub
		String path=generateStatsFile();
		File stats=new File(path);
		if(!stats.exists()){
			System.out.println("create empty file");
			try {
				stats.createNewFile();
				PrintWriter writer = new PrintWriter(path, "UTF-8");
				String statContent =
						String.format("%s\n%s\n%s\n%s\n%s\n",
						"win:"+"0  ", 
						"lose:"+"0  ", 
						"tie:"+"0  ",
						"total:"+"0  ", 
						"percentage:"+"0"+"%");
				byte[] statsContent =statContent.getBytes(StandardCharsets.UTF_8);
				String encodedStat=Base64.getEncoder().encodeToString(statsContent);
				writer.print(encodedStat);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new StorageException("Unable to create initial stats file");
			}
		}
	}


	public static void generateStats(int result) throws IOException {
		// TODO Auto-generated method stub
		int[] array=new int[3];
		String path=generateStatsFile();
		System.out.println("file exists");
		try {
			String content;
			content = Game.readstats(new File(StorageManager.generateStatsFile()));
			System.out.print(content);
			array=Game.extractnumbers(content);
			if(result==1){
				winNum=array[0]+1;
				}
			else if(result==2){
				loseNum=array[1]+1;
				System.out.println("lose"+loseNum);
			}
			else if(result==0){
				tie=array[2]+1;
			}
			int total= winNum+loseNum+tie;
			if(total==0){
			percentage=0;	
			}
			else{
             percentage = ((float) winNum) / ((float) total)*100;
              }
			//System.out.println("yeah");
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			String statContent =
					String.format("%s\n%s\n%s\n%s\n%s\n",
					"win:"+winNum+"  ", 
					"lose:"+loseNum+"  ", 
					"tie:"+tie+"  ",
					"total:"+total+"  ", 
					"percentage:"+percentage+"%");
			byte[] statsContent =statContent.getBytes(StandardCharsets.UTF_8);
			String encodedStat=Base64.getEncoder().encodeToString(statsContent);
			writer.print(encodedStat);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	public static String generateStatsFile() {
		// TODO Auto-generated method stub
		String fileName="stats.txt";
		byte[] authBytes=fileName.getBytes(StandardCharsets.UTF_8);
		String encoded=Base64.getEncoder().encodeToString(authBytes);
		String path= CONFIG+"\\"+encoded;
		return path;
		
	}



	public static int getwinNum(){
		return winNum;
	}
	public static int getloseNum(){
		return loseNum;
	}
	public static float getpercent(){
		return percentage;
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
							"and the time limit for singleplayer games and multiplayer games are in seconds.",
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
	
	public static Map<String, Object> getOptionsMapping() throws XMLException {
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
	
	private static Map<String, Object> getOptionsMapping(String optionsString) throws XMLException {
		// Root element
		XMLElement optionsElement = null;
		optionsElement = XMLHelper.strToXML(optionsString);
		
		// Parent elements
		XMLElement general = tryAndGetChild(optionsElement, "General");
		XMLElement singlePlayerGame = tryAndGetChild(optionsElement, "SingleplayerGame");
		XMLElement multiplayerGame = tryAndGetChild(optionsElement, "MultiplayerGame");
		XMLElement networkGame = tryAndGetChild(optionsElement, "NetworkGame");
		XMLElement AIGame = tryAndGetChild(optionsElement, "AIGame");
		XMLElement analysisGame = tryAndGetChild(optionsElement, "AnalysisGame");
		
		// Children elements
		XMLElement boardWidth = tryAndGetChild(general, "BoardWidth");
		XMLElement backgroundColor = tryAndGetChild(general, "BackgroundColor");
		XMLElement recordAutoSave = tryAndGetChild(general, "RecordAutoSave");
		XMLElement timedGame = tryAndGetChild(singlePlayerGame, "TimedGame");
		XMLElement timeLimit = tryAndGetChild(singlePlayerGame, "TimeLimit");
		XMLElement withdrawalLimitSingle = tryAndGetChild(singlePlayerGame, "WithdrawalLimit");
		XMLElement playerName = tryAndGetChild(networkGame, "PlayerName");
		XMLElement responseInterval = tryAndGetChild(AIGame, "ResponseInterval");
		XMLElement animationInterval = tryAndGetChild(analysisGame, "AnimationInterval");
		XMLElement withdrawalLimitMulti = tryAndGetChild(multiplayerGame, "WithdrawalLimit");
		
		// Contents of configuration
		try{
			int width = Integer.parseInt(boardWidth.getContent().trim());
			String color = backgroundColor.getContent().trim();
			boolean autoSave = Boolean.parseBoolean(recordAutoSave.getContent().trim());
			boolean timedGameOption = timedGame.getContent().equals("Enabled");
			int timeLimitInt = Integer.parseInt(timeLimit.getContent().trim());
			int withdrawalLimitS = Integer.parseInt(withdrawalLimitSingle.getContent().trim());
			int withdrawalLimitM = Integer.parseInt(withdrawalLimitMulti.getContent().trim());
			String playerNameStr = playerName.getContent().trim();
			int responseIntervalInt = Integer.parseInt(responseInterval.getContent().trim());
			int animationIntervalInt = Integer.parseInt(animationInterval.getContent().trim());
			
			if (width < 15 || width > 30)
				throw new XMLException("Invalid board width");
			if (withdrawalLimitS < 0 || withdrawalLimitS > 4)
				throw new XMLException("Invalid withdrawal limit for single player game.");
			if (withdrawalLimitM < 0 || withdrawalLimitM > 3)
				throw new XMLException("Invalid withdrawal limit for multiplayer game.");
			if (!Options.isPlayerNameValid(playerNameStr))
				throw new XMLException("Invalid player's name.");
			if (timeLimitInt < 60 || timeLimitInt > 6000)
				throw new XMLException("Invalid time limit.");
			if (animationIntervalInt < 200 || animationIntervalInt > 10000)
				throw new XMLException("Invalid animation interval.");
			if (responseIntervalInt < 0 || responseIntervalInt > 8000)
				throw new XMLException("Invalid response interval.");
			
			// Instantiate maps
			Map<String, Object> generalsMap = new HashMap<String, Object>();
			Map<String, Object> singleplayerMap = new HashMap<String, Object>();
			Map<String, Object> multiplayerMap = new HashMap<String, Object>();
			Map<String, Object> aiGameMap = new HashMap<String, Object>();
			Map<String, Object> networkGameMap = new HashMap<String, Object>();
			Map<String, Object> analysisGameMap = new HashMap<String, Object>();
			
			// Put keys into sub maps.
			generalsMap.put("boardWidth", width);
			generalsMap.put("backgroundColor", color);
			generalsMap.put("recordAutoSave", autoSave);
			singleplayerMap.put("timedGame", timedGameOption);
			singleplayerMap.put("timeLimit", timeLimitInt);
			singleplayerMap.put("withdrawalLimit", withdrawalLimitS);
			multiplayerMap.put("withdrawalLimit", withdrawalLimitM);
			networkGameMap.put("playerName", playerNameStr);
			analysisGameMap.put("animationInterval", animationIntervalInt);
			aiGameMap.put("responseInterval", responseIntervalInt);
			
			// Put children maps into retVal;
			Map<String, Object> retVal = new HashMap<String, Object>();
			retVal.put("general", generalsMap);
			retVal.put("singleplayerGame", singleplayerMap);
			retVal.put("multiplayerGame", multiplayerMap);
			retVal.put("networkGame", networkGameMap);
			retVal.put("AIGame", aiGameMap);
			retVal.put("analysisGame", analysisGameMap);
			
			return retVal;
		} catch (Exception e) {
			throw new XMLException("The option parameters are invalid.");
		}
	}
	
	private static XMLElement tryAndGetChild(XMLElement ele, String childName) throws XMLException {
		XMLElement retVal = ele.getFirstChild(childName);
		if (retVal == null)
			throw new XMLException("Child not found.");
		
		return retVal;
	}
}
