package Model;

public final class ServerConstants {
	/**
	 * Server request contants.
	 */
	public static final String MESSAGE_REQUEST = "Message";
	public static final String GIVEUP_REQUEST = "Surrender";
	public static final String MOVE_REQUEST = "Move";
	public static final String WITHDRAW_REQUEST = "Withdraw";
	public static final String TIE_REQUEST = "Tie";
	public static final String GAME_START = "Start";

	/**
	 * Server response constants.
	 */
	public static final int REQUEST_OK = 1;
	public static final int MOVE_SQUARE_OCCUPIED = 2;
	public static final int MOVE_OUT_BOUND = 3;
	public static final int PEER_DISCONNECTED = 4;
	public static final int WITHDRAW_DECLINED = 5;
	public static final int OTHER_PLAYER_MOVE = 6;
	public static final int NOT_YOUR_TURN = 7;

	public static final int MAX_CAPACITY = 10;
}
