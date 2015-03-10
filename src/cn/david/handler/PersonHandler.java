package cn.david.handler;

import org.apache.log4j.Logger;

import cn.david.domain.AddressBookProtos;
import cn.david.domain.AddressBookProtos.Person;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PersonHandler extends ChannelInboundHandlerAdapter {
	Logger logger = Logger.getLogger(PersonHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.info("Come into the PersonHandler!");
		Person person = (Person) msg;
		System.out.println(person.getName());
		System.out.println(person.getEmail());
		System.out.println(person.getId());
		System.out.println(person.getPhone(0).getNumber());
		System.out.println(person.getPhone(0).hasType());
	}
}
