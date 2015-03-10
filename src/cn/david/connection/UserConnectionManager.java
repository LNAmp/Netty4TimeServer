package cn.david.connection;

import java.util.concurrent.ConcurrentHashMap;


public class UserConnectionManager {
	// 类中存储的是userid->UserConnection的key/value pair
	//可以查询该服务器上有无该用户的连接
	//这么做相当于懒加载，没有线程安全问题
	private final static ConcurrentHashMap<Integer, UserConnection> userConnMap = new ConcurrentHashMap<Integer, UserConnection>();
	
	public final static void addUserConnection(Integer userId, UserConnection userConn) {
		userConnMap.put(userId, userConn);
	}
	
	public final static boolean containUserConn(Integer userId) {
		return userConnMap.containsKey(userId);
	}
	
	public final static UserConnection getUserConnection(Integer userId) {
		return userConnMap.get(userId);
	}
	
	public final static void removeUserConnection(Integer userId) {
		userConnMap.remove(userId);
	}
}
