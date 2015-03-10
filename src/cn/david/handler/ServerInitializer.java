package cn.david.handler;

import org.apache.log4j.Logger;

import cn.david.codec.MyProtobufFrameDecoder;
import cn.david.domain.AddressBookProtos.Person;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	Logger logge = Logger.getLogger(ServerInitializer.class);
	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline p = socketChannel.pipeline();
//		p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
//		p.addLast("protobufDecoder", new ProtobufDecoder(Person.getDefaultInstance()));
//		p.addLast("msgHandler",new PersonHandler());
		p.addLast("frameDecoder", new MyProtobufFrameDecoder());
	}
}
