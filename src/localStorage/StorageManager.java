package localStorage;

import java.io.File;

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

	public static void initializeStorage() throws StorageException {
		File gameMainDir = new File(DIR);
		if (gameMainDir.exists()) {
			File gameRecordsDir = new File(RECORD);
			File gameTokensDir = new File(TOKEN);
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
	}

	public static void storeToken(byte[] token) {

	}

	public static void storeGameRecord(String record, String gameHash) {

	}
}
