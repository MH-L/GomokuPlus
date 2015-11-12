package localStorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import exceptions.StorageException;

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

	public static void storeGameRecord(String record, String gameHash) {

	}
}
