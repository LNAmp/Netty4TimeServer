package cn.david.handler;

import org.apache.log4j.Logger;

import cn.david.codec.MsgFiledBasedProtobufFrameDecoder;
import cn.david.codec.MsgFiledBasedProtobufFrameEncoder;
import cn.david.constants.LengthBasedFrameConstants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class IMServerInitializer extends ChannelInitializer<SocketChannel> {

	Logger logger = Logger.getLogger(IMServerInitializer.class);
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		LengthFieldBasedFrameDecoder frameDecoder = new LengthFieldBasedFrameDecoder(
				LengthBasedFrameConstants.MAX_FRAME_LENGTH,
				LengthBasedFrameConstants.LENGTH_FIELD_OFFSET, 
				LengthBasedFrameConstants.LENGTH_FIELD_LENGTH, 
				-1*LengthBasedFrameConstants.LENGTH_FIELD_LENGTH,
				LengthBasedFrameConstants.LENGTH_FIELD_LENGTH);
		LengthFieldPrepender frameEncoder = new LengthFieldPrepender(
				LengthBasedFrameConstants.LENGTH_FIELD_LENGTH, true);
		
		logger.info("Init the channel pipeline.");
		p.addLast("frameDecoder", frameDecoder);
		p.addLast("protobufDecoder", new MsgFiledBasedProtobufFrameDecoder());
		p.addLast("frameEncoder", frameEncoder);
		p.addLast("protobufEncoder", new MsgFiledBasedProtobufFrameEncoder());
		p.addLast("msgHandler", new IMServerMsgHandler());
	}

}
