package lmh.gomoku.auth;

public class AuthConstants {
	/**
	 * Requests from the client.
	 */
	public static final int INT_CREATE_ACCOUNT = 1000;
	public static final int INT_LOG_IN = 1001;
	public static final int INT_CHANGE_PASS = 1002;
	public static final int INT_CHANGE_UNAME = 1003;

	/**
	 * Success messages constant.
	 */
	public static final int INT_ACTION_SUCCESS = 2000;

	/**
	 * Error messages constant.
	 */
	public static final int INT_GENERAL_ERROR = 4000; // general error

	/**
	 * Client side request.
	 */
	public static final String STR_TOKEN = "Token";
}
