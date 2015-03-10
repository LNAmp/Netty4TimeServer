package cn.david.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import cn.david.domain.ChatMessage;
import cn.david.domain.ChatType;
import cn.david.util.RedisUtil;

import com.alibaba.fastjson.JSON;

public class OfflineMsgTest {
	
	@Test
	public void jsonConverter() {
		ChatMessage msg = new ChatMessage();
		msg.setChatType(ChatType.IMAGE);
		msg.setContent("i love you,yiqian");
		msg.setMsgId("123");
		String json = JSON.toJSONString(msg);
		System.out.println("after fastJson, the json is: " + json);
		ChatMessage msg2 = JSON.parseObject(json, ChatMessage.class);
		System.out.println("the original obj is: "+msg2);
	}
	
	@Test
	public void insertMsgTest(){
		//ChatM
		Jedis jedis = RedisUtil.getJedis();
		jedis.zremrangeByScore("offlinemsg:1", Double.MIN_VALUE,Double.MAX_VALUE);
		ChatMessage msg = new ChatMessage();
		msg.setChatType(ChatType.IMAGE);
		msg.setContent("i love you,yiqian");
		msg.setMsgId("123");
		insertMsg(1, msg);
		msg.setChatType(ChatType.TEXT);
		insertMsg(1,msg);
		msg.setContent("i love you,yiqian forever");
		insertMsg(1,msg);
		
		Set<String>  msgs = jedis.zrangeByScore("offlinemsg:1", Double.MIN_VALUE,Double.MAX_VALUE);
		for(String json : msgs) {
			ChatMessage msg2 = JSON.parseObject(json, ChatMessage.class);
			System.out.println(msg2);
		}
	}
	
	private void insertMsg(int userId,ChatMessage msg) {
		Jedis jedis = RedisUtil.getJedis();
		long num = jedis.zcount("offlinemsg:"+userId, 0.0, Double.MAX_VALUE);
		int seconds = (int)(System.currentTimeMillis()/1000);
		long score = ((long)seconds << 20) + (int)num;
		System.out.println("num:" + num + ",score:"+score);
		String json = JSON.toJSONString(msg);
		jedis.zadd("offlinemsg:"+userId, (double)score, json);
		RedisUtil.releaseJedis(jedis);
	}
	
	
	//取完就应该删掉了
	private List<ChatMessage> getAllOfflineMsg(int userId) {
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
	
	@Test
	public void test3() {
		List<ChatMessage> msgs =  getAllOfflineMsg(2);
		for(ChatMessage msg : msgs ) {
			System.out.println(msg);
		}
	}
}
