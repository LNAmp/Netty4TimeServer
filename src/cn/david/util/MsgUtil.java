package cn.david.util;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import cn.david.domain.IMProto.AckMsg;
import cn.david.domain.IMProto.AskLocAckMsg;
import cn.david.domain.IMProto.ChatAckMsg;
import cn.david.domain.MsgType;
import cn.david.domain.ServerMsg;

public class MsgUtil {
	private static Logger logger = Logger.getLogger(MsgUtil.class);
	
	/**
	 * 由于对于Login来说，需要返回一个token
	 * @param ctx
	 * @param type
	 * @param code
	 * @param msgId
	 */
	@Deprecated
	public static void sendAckMsg(ChannelHandlerContext ctx, int type, int code, String msgId) {
		ServerMsg msg = new ServerMsg();
		msg.setMsgType(MsgType.ACK);
		AckMsg.Builder builder = AckMsg.newBuilder();
		builder.setResponseCode(code)
		.setAckType(type);
		if(msgId != null) {
			builder.setMsgId(msgId);
		}
		msg.setProtoMsgContent(builder.build());
		ctx.writeAndFlush(msg);
	}
	
	public static void sendAckMsg(ChannelHandlerContext ctx, AckMsg ackMsg) {
		ServerMsg msg = new ServerMsg();
		msg.setMsgType(MsgType.ACK);
		msg.setProtoMsgContent(ackMsg);
		ctx.writeAndFlush(msg);
	}
	
	/**
	 * 发送chat的ack信息
	 * @param ctx
	 * @param code
	 * @param msgId
	 */
	public static void sendChatAckMsg(ChannelHandlerContext ctx, int code , String msgId) {
		ServerMsg msg = new ServerMsg();
		ChatAckMsg.Builder builder = ChatAckMsg.newBuilder();
		builder.setMsgId(msgId)
		.setResponseCode(code)
		.setRecTime(System.currentTimeMillis());
		msg.setMsgType(MsgType.CHAT_ACK);
		msg.setProtoMsgContent(builder.build());
		ctx.writeAndFlush(msg);
	}
	
	public static void sendLocAckMsg(ChannelHandlerContext ctx, AskLocAckMsg locMsg) {
		ServerMsg msg = new ServerMsg();
		msg.setMsgType(MsgType.ASK_LOCATION_ACK);
		msg.setProtoMsgContent(locMsg);
		ctx.writeAndFlush(msg);
	}
}
