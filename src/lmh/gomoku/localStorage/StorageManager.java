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
			generateStats();
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	public static void generateStats() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("inside");
		File stats=new File(CONFIG+"\\STATS.txt");
		//byte[] bytes=Encoding.
		if(stats.createNewFile()){
			System.out.println("ye");
			int winNum=5;
			int loseNum=7;
			double percentage=0.41;
			System.out.println("yeah");
			PrintWriter writer = new PrintWriter(CONFIG+"\\STATS.txt", "UTF-8");
			String readmeContent =
					"win:"+winNum+", lose:"+loseNum+", percentage:"+percentage;
			writer.print(readmeContent);
			writer.close();
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
