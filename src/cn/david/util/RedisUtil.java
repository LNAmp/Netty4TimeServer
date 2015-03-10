package cn.david.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import cn.david.domain.ChatMessage;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	private static JedisPool pool;
	
	static {
		//配置pool
		ResourceBundle bundle = ResourceBundle.getBundle("redis");
		if(bundle == null) {
			throw new IllegalArgumentException("can't find the [redis.properties]");
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));
		pool = new JedisPool(config,
				bundle.getString("redis.ip"),
				Integer.valueOf(bundle.getString("redis.port")));
	}
	
	/**
	 * 从池中得到一个Jedis对象
	 * @return
	 */
	public static Jedis getJedis() {
		return pool.getResource();
	}
	
	/**
	 * 将Jedis对象放回池中
	 * @param jedis
	 */
	
	public static void releaseJedis(Jedis jedis) {
		pool.returnResource(jedis);
	}
	
	/**
	 * 增加对应user 的offline msg
	 * @param userId
	 * @param msg
	 */
	public static void insertOfflineMsg(int userId,ChatMessage msg) {
		Jedis jedis = RedisUtil.getJedis();
		long num = jedis.zcount("offlinemsg:"+userId, Double.MIN_VALUE, Double.MAX_VALUE);
		int seconds = (int)(System.currentTimeMillis()/1000);
		//此处更新sendtime
		msg.setSendTime(seconds);
		long score = ((long)seconds << 20) + (int)num;
		//System.out.println("num:" + num + ",score:"+score);
		String json = JSON.toJSONString(msg);
		jedis.zadd("offlinemsg:"+userId, (double)score, json);
		RedisUtil.releaseJedis(jedis);
	}
	
	/**
	 * 取出对应user 的所有offline msg
	 * @param userId
	 * @return
	 */
	public static List<ChatMessage> getAllOfflineMsg(int userId) {
		Jedis jedis = RedisUtil.getJedis();
		long num = jedis.zcount("offlinemsg:"+userId, Double.MIN_VALUE, Double.MAX_VALUE);
		Set<String>  msgs = jedis.zrangeByScore("offlinemsg:"+userId, Double.MIN_VALUE,Double.MAX_VALUE);
		//删除所有的offline信息
		jedis.zremrangeByScore("offlinemsg:"+userId, Double.MIN_VALUE,Double.MAX_VALUE);
		List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>((int)num);
		for(String json : msgs) {
			ChatMessage msg2 = JSON.parseObject(json, ChatMessage.class);
			chatMsgs.add(msg2);
		}
		RedisUtil.releaseJedis(jedis);
		return chatMsgs;
	}
	
}
