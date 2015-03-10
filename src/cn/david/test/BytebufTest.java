package cn.david.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.Test;

import cn.david.domain.LocationType;
import cn.david.domain.IMProto.UploadLocMsg;
import cn.david.domain.MsgType;

import com.google.protobuf.InvalidProtocolBufferException;

public class BytebufTest {
	
	@Test
	public void test1() throws UnsupportedEncodingException, InvalidProtocolBufferException {
		UploadLocMsg.Builder bulider = UploadLocMsg.newBuilder();
		UploadLocMsg locMsg = bulider.setLocationType(LocationType.NETWORK)
		.setUserId(12)
		.setLongitude(128.32)
		.setLatitude(29.00)
		.setMapId("F2B4")
		.setUpdateTime(new Date().getTime()).build();
		
		byte[] locMsgBytes = locMsg.toByteArray();
		System.out.println("the length is : " + locMsgBytes.length);
		
		byte[] msgTypeBytes = MsgType.UPLOAD_LOCATION.getBytes("UTF-8");
		ByteBuf buf = Unpooled.wrappedBuffer(msgTypeBytes,locMsgBytes);
		System.out.println(buf.readableBytes());
		if(buf.hasArray()) {
			System.out.println("hasArray before");
		}
		byte[] b = new byte[2];
		buf.readBytes(b, 0, 2);
		System.out.println(new String(b,"UTF-8"));
		System.out.println("the remained length is : " + buf.readableBytes());
		int length = buf.readableBytes();
		byte[] array = new byte[length];
		buf.readBytes(array, 0, length);
		UploadLocMsg msg2 = UploadLocMsg.getDefaultInstance().getParserForType().parseFrom(array, 0, length);
		System.out.println(msg2.toString());
//		ByteBuf buf = Unpooled.wrappedBuffer(msgTypeBytes, locMsgBytes);
//		System.out.println(buf.readableBytes());
//		if(buf.hasArray()) {
//			System.out.println("hasArray after");
//		}
		
//		UploadLocMsg msg2 = null;
//		try {
//			msg2 = UploadLocMsg.getDefaultInstance().parseFrom(locMsgBytes);
//		} catch (InvalidProtocolBufferException e) {
//			e.printStackTrace();
//		}
	}
}
