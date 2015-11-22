package lmh.gomoku.exception;

public class StorageException extends Exception {
	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -6960000141358531333L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException() {
		super("Unable to access storage.");
	}
}
