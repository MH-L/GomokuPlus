package localStorage;

public class StorageManager {
	/**
	 * The game's root directory.
	 */
	private static final String DIR = "C:\\Program Files\\Gomoku Plus";
	/**
	 * Directory for game records.
	 */
	private static final String RECORD = DIR + "\\records";
	/**
	 * Directory for login tokens.
	 */
	private static final String TOKEN = DIR + "\\tokens";
	public static boolean initialize() {
		return false;
	}
}
