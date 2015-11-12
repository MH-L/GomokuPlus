package exceptions;

/**
 * Thrown when config helper cannot find config specified by the key
 * in the config file.
 * @author Minghao
 *
 */
public class NoSuchConfigException extends Exception {
	/**
	 * Generated serializationID.
	 */
	private static final long serialVersionUID = 8053455823028626944L;

	public NoSuchConfigException(String message) {
		super(message);
	}
}
