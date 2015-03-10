package cn.david.task;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

import org.apache.log4j.Logger;

import cn.david.connection.UserConnection;
import cn.david.connection.UserConnectionManager;
import cn.david.domain.ChatMessage;
import cn.david.domain.IMProto.ChatMsg;
import cn.david.domain.MsgType;
import cn.david.domain.ServerMsg;

public class ChatMsgRouteTask implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChatMessage msg;
	private transient Logger logger = Logger.getLogger(ChatMsgRouteTask.class);
	
	public ChatMsgRouteTask(ChatMessage msg) {
		this.msg = msg;
	}
	
	@Override
	public void run() {
		if(!UserConnectionManager.containUserConn(msg.getUserIdDesc())) {
			//MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msgId)
			//如果不在真是服务器错误啊
			logger.error("server error!");
			return;
		}
		//iosession在该台服务器上，则可以获得ctx，然后将信息封装后发送出去
		UserConnection bConn = UserConnectionManager.getUserConnection(msg.getUserIdDesc());
		ChannelHandlerContext ctxB = bConn.getCtx();
		ChatMsg chatMsg = msg.toChatMsg();
		ServerMsg bMsg = new ServerMsg();
		bMsg.setMsgType(MsgType.CHAT);
		bMsg.setProtoMsgContent(chatMsg);
		ctxB.writeAndFlush(bMsg);
		//此处欠缺发送回复消息
	}

}
