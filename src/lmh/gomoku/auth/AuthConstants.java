package lmh.gomoku.auth;

public class AuthConstants {
	/**
	 * Requests from the client.
	 */
	public static final int INT_CREATE_ACCOUNT = 1000;
	public static final int INT_LOG_IN_PASSWORD = 1001;
	public static final int INT_CHANGE_PASS = 1002;
	public static final int INT_CHANGE_UNAME = 1003;
	public static final int INT_LOG_IN_TOKEN = 1004;

	/**
	 * Success messages constant.
	 */
	public static final int INT_ACTION_SUCCESS = 2000;
	// If login succeeds, server sends the token to the user.
	// And INT_ACTION_SUCCESS will not be sent.
	public static final int INT_YOUR_TOKEN = 2001;

	/**
	 * Error messages constant.
	 */
	public static final int INT_GENERAL_ERROR = 4000; // general error

	/**
	 * Client side request.
	 */
	public static final String STR_TOKEN = "Token";
}
