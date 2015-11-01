package Model;

public final class ServerConstants {
	/**
	 * Server request contants.
	 */
	public static final String STR_MESSAGE_REQUEST = "Message";
	public static final String STR_GIVEUP_REQUEST = "Surrender";
	public static final String STR_MOVE_REQUEST = "Move";
	public static final String STR_WITHDRAW_REQUEST = "Withdraw";
	public static final String STR_TIE_REQUEST = "Tie";
	public static final String STR_GAME_START = "Start";

	/**
	 * Server response constants.
	 */
	public static final int INT_REQUEST_OK = 1;
	public static final int INT_MOVE_SQUARE_OCCUPIED = 2;
	public static final int INT_MOVE_OUT_BOUND = 3;
	public static final int INT_PEER_DISCONNECTED = 4;
	public static final int INT_WITHDRAW_DECLINED = 5;
	public static final int INT_OTHER_PLAYER_MOVE = 6;
	public static final int INT_NOT_YOUR_TURN = 7;
	public static final int INT_VICTORY = 8;
	public static final int INT_DEFEAT = 9;
	public static final int INT_TIE = 10;
	public static final int INT_WITHDRAW_MESSAGE = 11;
	public static final int INT_WITHDRAW_APPROVED = 12;

	public static final int MAX_CAPACITY = 10;
}
