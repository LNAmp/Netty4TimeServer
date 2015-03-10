package cn.david.session;

import java.util.Date;

import cn.david.util.SessionUtil;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class SessionManager {
	
	private static MemCachedClient client = new MemCachedClient();
	
	static {
		String[] servers = {"10.108.250.115:11111"};
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(servers);
		pool.setFailover(true);
		pool.setInitConn(10);
		pool.setMinConn(5);
		pool.setMaxConn(100);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setAliveCheck(true);
		pool.initialize();
	}
	
	public static synchronized UserSession getUserSession(int userId) {
		return (UserSession) client.get(SessionUtil.generateSessionId(userId));
	}
	
	public static synchronized void setUserSession(int userId, UserSession session) {
		client.set(SessionUtil.generateSessionId(userId), session);
	}
	public static synchronized void setUserSession(int userId, UserSession session, Date expire) {
		client.set(SessionUtil.generateSessionId(userId), session, expire);
	}
	
	public static synchronized void commitUserSession(String sessionId, UserSession session) {
		client.set(sessionId, session);
	}
}
