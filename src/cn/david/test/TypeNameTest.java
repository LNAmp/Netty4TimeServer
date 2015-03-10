package cn.david.test;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import org.junit.Test;

import com.google.protobuf.Descriptors;

import cn.david.domain.AddressBookProtos;
import cn.david.domain.AddressBookProtos.Person;
import cn.david.util.DateUtil;

public class TypeNameTest {

	@Test
	public void test() {
		String typeName =AddressBookProtos.Person.getDescriptor().getFullName();
		System.out.println(typeName);
		typeName = AddressBookProtos.AddressBook.getDescriptor().getFullName();
		System.out.println(typeName);
	}
	
	@Test
	public void test1() {
		LengthFieldBasedFrameDecoder a = null;
		LengthFieldPrepender b = null;
	}
	
	@Test 
	public void test2() {
		System.out.println(DateUtil.printCurTime());
	}
	
}
