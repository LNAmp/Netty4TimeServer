package cn.david.util;

import java.util.UUID;

public class SessionUtil {
	private static String feed = "david";
	
	public static String generateSessionId(int userId) {
		String userFeed = userId + "_" + feed;
		//String encoded = Md5Util.encode(userFeed);
		return userFeed;
	}
	
	public static String generateToken(int userId) {
		return userId + "" + UUID.randomUUID().toString();
	}
}
