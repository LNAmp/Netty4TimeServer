package cn.david.codec;

import java.util.List;

import cn.david.protobuf.MsgProp;
import cn.david.protobuf.MsgPropTable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.CharsetUtil;

public class MyProtobufFrameDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		//先读出总的帧长度
		in.markReaderIndex();
		if(in.readableBytes() < 4) {
			in.resetReaderIndex();
			return;
		}
		int frameLen = in.readInt();
		if(in.readableBytes() < (frameLen-4)) {
			in.markReaderIndex();
			return;
		}
		//读出消息类型
		byte[] msgBytes = new byte[1];
		in.readBytes(msgBytes, 0, 1);
		String msgType = new String(msgBytes,"UTF-8");
		MsgProp msgProp = MsgPropTable.getMsgProp(msgType);
		//根据消息类型查找消息解码器和消息handler并添加或者替换
		ProtobufDecoder protobufDecoder = new ProtobufDecoder(msgProp.getProtoType());
		ChannelHandler msgHandler = (ChannelHandler) Class.forName(msgProp.getHandlerFullName()).newInstance();
		ChannelPipeline pipeline = ctx.pipeline();
		if(pipeline.get("protobufDecoder") != null) {
			pipeline.replace("protobufDecoder", "protobufDecoder", protobufDecoder);
		} else {
			pipeline.addLast("protobufDecoder", protobufDecoder);
		}
		if(pipeline.get("msgHandler") != null) {
			pipeline.replace("msgHandler", "msgHandler", msgHandler);
		} else {
			pipeline.addLast("msgHandler", msgHandler);
		}
		if(in.readableBytes() < (frameLen-5)) {
			in.markReaderIndex();
			return;
		} else {
			out.add(in.readBytes(frameLen-5));
		}
		
	}
	
}
