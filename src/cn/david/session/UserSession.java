package cn.david.session;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import cn.david.util.SessionUtil;

public class UserSession implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userId;
	private String sessionId;
	private Map<String, Object> attributes = new HashMap<String,Object>();
	
	
	public UserSession(int userId) {
		this.userId = userId;
		this.sessionId = SessionUtil.generateSessionId(userId);
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	public UserSession setAttribute(String name,Object value) {
		attributes.put(name, value);
		return this;
	}
	
	public UserSession removeAttribute(String name) {
		attributes.remove(name);
		return this;
	}
	
	public void invalidate(int mins) {
		if(mins < 0) {
			throw new IllegalArgumentException("the mins can't be negative.");
		}
		long expire = System.currentTimeMillis() + (long)mins * 60 * 1000;
		Date expireDate = new Date(expire);
		SessionManager.setUserSession(userId, this, expireDate);
		
	}
	
	public void commit() {
		SessionManager.commitUserSession(sessionId, this);
	}
}
