package lmh.gomoku.application;

import java.util.Map;

import javax.swing.JOptionPane;

import lmh.gomoku.exception.XMLException;
import lmh.gomoku.localStorage.StorageManager;

/**
 * A class for the options. In order to have fewer access to 
 * the local storage files (which are on the disk), an options 
 * object needs to be placed in Main for other parts of application
 * to access to. This class is a singleton because there could only
 * be one options object at any time.
 * @author Minghao
 *
 */
public class Options {
	private static Options instance = null;
	Map<String, Object> optionsMapping = null;
	
	private Options() {
		try {
			optionsMapping = StorageManager.getOptionsMapping();
		} catch (XMLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Malformed Options", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public Options getOption() {
		if (instance == null)
			return new Options();
		else
			return instance;
	}
	
	/**
	 * Checks if a player's name is valid. A player's name can only contain 
	 * upper/lower case letters, numbers and underscores.
	 * @param playerName player's name
	 * @return true if name valid, false otherwise
	 */
	public static boolean isPlayerNameValid(String playerName) {
		String pattern = "^[a-zA-Z0-9_]*$";
		if (playerName.matches(pattern))
			return true;
		return false;
	}
}
