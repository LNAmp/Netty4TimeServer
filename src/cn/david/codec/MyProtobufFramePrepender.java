package cn.david.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyProtobufFramePrepender extends MessageToByteEncoder<ByteBuf> {
	
	private String msgType ;
	public MyProtobufFramePrepender(String msgType) {
		this.msgType = msgType;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
			throws Exception {
		int bodyLen = msg.readableBytes();
		int headerLen = 4 + 1;
		out.ensureWritable(bodyLen + headerLen);
		out.writeInt(headerLen + bodyLen);
		out.writeBytes(msgType.getBytes("UTF-8"));
		out.writeBytes(msg, msg.readerIndex(), bodyLen);
	}

}
