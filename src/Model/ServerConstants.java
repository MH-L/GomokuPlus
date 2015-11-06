package Model;

public final class ServerConstants {
	/**
	 * Server request contants.
	 */
	public static final String STR_ONLINE = "Online";
	public static final String STR_QUIT = "Quit";
	public static final String STR_MESSAGE_REQUEST = "Message";
	public static final String STR_GIVEUP_REQUEST = "Surrender";
	public static final String STR_MOVE_REQUEST = "Move";
	public static final String STR_WITHDRAW_REQUEST = "Withdraw";
	public static final String STR_TIE_REQUEST = "Tie";
	public static final String STR_REQUEST_GAME_START = "Start";
	public static final String STR_TIE_APPROVED = "ApprovedTie";
	public static final String STR_TIE_DECLINED = "DeclineTie";
	public static final String STR_WITHDRAW_DECLINED = "DeclineWithdraw";
	public static final String STR_WITHDRAW_APPROVED = "ApprovedWithdraw";
	public static final String STR_AVAILABLE = "Available";

	/**
	 * Server response constants.
	 */
	public static final int INT_REQUEST_OK = 1;
	public static final int INT_MOVE_SQUARE_OCCUPIED = 2;
	public static final int INT_MOVE_OUT_BOUND = 3;
	public static final int INT_PEER_DISCONNECTED = 4;
	public static final int INT_WITHDRAW_DECLINED = 5;
	public static final int INT_OPPONENT_MOVE = 6;
	public static final int INT_NOT_YOUR_TURN = 7;
	public static final int INT_VICTORY = 8;
	public static final int INT_DEFEAT = 9;
	public static final int INT_TIE = 10;
	public static final int INT_WITHDRAW_MESSAGE = 11;
	public static final int INT_WITHDRAW_APPROVED = 12;
	public static final int INT_PEER_CONNECTED = 13;
	public static final int INT_GAME_START_APPORVED = 14;
	public static final int INT_SENTE = 16;
	public static final int INT_GOTE = 17;
	public static final int INT_YOUR_MOVE = 19;
	public static final int INT_TIE_PROPOSED = 20;
	public static final int INT_TIE_DECLINED = 21;
	public static final int INT_WITHDRAW_FAILED = 22;

	public static final int MAX_CAPACITY = 10;
}
