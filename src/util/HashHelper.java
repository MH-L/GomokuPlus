package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
	private static MessageDigest digest;
	private static HashHelper instance;

	private HashHelper() {
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("The algorithm is not available.");
		}
	}

	public static HashHelper getInstance() {
		if (instance == null) {
			return new HashHelper();
		} else {
			return instance;
		}
	}

	public static byte[] encrypt(String s) {
		digest.reset();
		digest.update(s.getBytes());
		return digest.digest();
	}
}
