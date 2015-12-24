package lmh.gomoku.application;

import java.util.HashMap;
import java.util.Map;

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
		optionsMapping = new HashMap<String, Object>();
	}
	
	public Options getOption() {
		if (instance == null)
			return new Options();
		else
			return instance;
	}
}
