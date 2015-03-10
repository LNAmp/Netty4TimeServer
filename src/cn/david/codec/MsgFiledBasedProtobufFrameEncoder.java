package cn.david.codec;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.MessageLite;

import cn.david.domain.ServerMsg;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class MsgFiledBasedProtobufFrameEncoder extends MessageToMessageEncoder<ServerMsg> {

	Logger logger = Logger.getLogger(MsgFiledBasedProtobufFrameEncoder.class);
	@Override
	protected void encode(ChannelHandlerContext ctx, ServerMsg msg,
			List<Object> out) throws Exception {
		//logger.info("Encoding... ");
		if( !(msg instanceof ServerMsg)) {
			throw new IllegalArgumentException("the msg is not the type of ServerMsg");
		}
		String msgType = msg.getMsgType();
		MessageLite msgProto = msg.getProtoMsgContent();
		byte[]  msgTypeBytes = msgType.getBytes("UTF-8");
		if(msgProto == null) {
			out.add(Unpooled.wrappedBuffer(msgTypeBytes));
		} else {
			out.add(Unpooled.wrappedBuffer(msgTypeBytes,msgProto.toByteArray()));
		}
	}

}
