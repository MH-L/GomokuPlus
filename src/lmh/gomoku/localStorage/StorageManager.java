package lmh.gomoku.localStorage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.List;

import lmh.gomoku.application.Game.Result;
import lmh.gomoku.database.ConnectionManager;
import lmh.gomoku.exception.StorageException;
import lmh.gomoku.model.Board;
import lmh.gomoku.model.IMove;
import lmh.gomoku.util.RecordCreator;

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
			//generateStats();
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	public static void generateStats(boolean Userwin) throws IOException {
		// TODO Auto-generated method stub
		String fileName="stats.xml";
		byte[] authBytes=fileName.getBytes(StandardCharsets.UTF_8);
		String encoded=Base64.getEncoder().encodeToString(authBytes);
		//System.out.println(fileName+"->"+encoded);
		File stats=new File(CONFIG+"\\"+encoded);
		if(!stats.exists()){
			System.out.println("create file");
			stats.createNewFile();
		}	
			if(Userwin){
				System.out.println("userwin "+winNum);
				winNum++;
				}
			else{
				System.out.println("userlose "+loseNum);
				loseNum++;
			}
			int total= winNum+loseNum;
			float percentage;
			if(total==0){
			percentage=0;	
			}
			else{
				System.out.println("totalnotzero");	
             percentage = ((float) winNum) / ((float) total)*100;
              }
			System.out.println("yeah");
			PrintWriter writer = new PrintWriter(CONFIG+"\\"+encoded, "UTF-8");
			String statContent =
					String.format("%s\n%s\n%s\n%s\n",
					"win:"+winNum, 
					"lose:"+loseNum, 
					"total:"+total, 
					"percentage:"+percentage+"%");
			byte[] statsContent =statContent.getBytes(StandardCharsets.UTF_8);
			String encodedStat=Base64.getEncoder().encodeToString(statsContent);
			writer.print(encodedStat);
			writer.close();
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
