//package cn.david.test;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.sql.SQLException;
//import java.util.Date;
//
//import org.junit.Test;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//
//import cn.david.db.DbcpUtil;
//import cn.david.db.ProtobufBeanHandler;
//import cn.david.db.QueryHelper;
//import cn.david.domain.ChatType;
//import cn.david.domain.IMProto;
//import cn.david.domain.IMProto.ChatMsg;
//import cn.david.domain.IMProto.GetLocMsg;
//import cn.david.domain.IMProto.UploadLocMsg;
//import cn.david.domain.LocationType;
//
//public class IMProtoTest {
////	
////	@Test
////	public void uploadLocMsgTest() {
////		UploadLocMsg.Builder bulider = UploadLocMsg.newBuilder();
////		UploadLocMsg locMsg = bulider.setLocationType(LocationType.NETWORK)
////		.setUserId(12)
////		.setLongitude(128.32)
////		.setLatitude(29.00)
////		.setMapId("F2B4")
////		.setUpdateTime(new Date().getTime()).build();
////		
////		byte[] locMsgBytes = locMsg.toByteArray();
////		System.out.println("the length is : " + locMsgBytes.length);
////		UploadLocMsg msg2 = null;
////		try {
////			msg2 = UploadLocMsg.getDefaultInstance().parseFrom(locMsgBytes);
////		} catch (InvalidProtocolBufferException e) {
////			e.printStackTrace();
////		}
////		System.out.println(msg2.toString());
////	}
////	
////	@Test
////	public void locMsgInsertDbTest() {
////		UploadLocMsg.Builder bulider = UploadLocMsg.newBuilder();
////		UploadLocMsg locMsg = bulider.setLocationType(LocationType.NETWORK)
////		.setUserId(13)
////		.setLongitude(128.32)
////		.setLatitude(29.00)
////		.setMapId("F3B4")
////		.setUpdateTime(new Date().getTime()).build();
////		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
////		int result = 0;
////		try {
////			result = qh.update("insert into location_upload (userId,longitude,latitude,mapId,createTime) values (?,?,?,?,?)", 
////					new Object[] {locMsg.getUserId(),locMsg.getLongitude(),locMsg.getLatitude(),locMsg.getMapId(),locMsg.getUpdateTime()});
////		} catch (SQLException e) {
////			e.printStackTrace();
////		}
////		if(result > 0) {
////			System.out.println("success!");
////		}else {
////			System.out.println("failed!");
////		}
////	}
////	
////	@Test
////	public void locMsgQueryDbTest() {
////		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
////		ProtobufBeanHandler beanHandler = new ProtobufBeanHandler(UploadLocMsg.newBuilder(), UploadLocMsg.Builder.class);
////		UploadLocMsg locMsg = null;
////		try {
////			locMsg = (UploadLocMsg) qh.query("select userId,longitude,latitude,mapId,createTime from location_upload where userId = ?", 
////					new Object[]{12}, beanHandler);
////		} catch (SQLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		System.out.println(locMsg.toString());
////	}
////	
////	@Test
////	public void getLocMsgTest() {
////		GetLocMsg.Builder bulider = GetLocMsg.newBuilder();
////		GetLocMsg locMsg = bulider.setLocMsgId("loc_011")
////		.setUserIdSrc(1)
////		.setUserIdDesc(2)
////		.setLocationType(LocationType.NETWORK)
////		.setLongitude(192.11)
////		.setLatitude(29.01)
////		.setMapId("F4B3")
////		.setUpdateTime(new Date().getTime()).build();
////		byte[] locMsgBytes = locMsg.toByteArray();
////		System.out.println("the length is: " + locMsgBytes.length );
////		
////		//GetLocMsg msg2 = null;
////		
//////		try {
//////			msg2 = GetLocMsg.getDefaultInstance().parseFrom(locMsgBytes);
//////		} catch (InvalidProtocolBufferException e) {
//////			e.printStackTrace();
//////		}
////		GetLocMsg.Builder b2 = locMsg.toBuilder();
////		b2.setLatitude(111.44);
////		locMsg = b2.build();
////		System.out.println(locMsg.toString());
////		//System.out.println(msg2.toString());
////	}
////	
////	@Test
////	public void chatMsgTest() throws ClassNotFoundException {
//////		//ChatBean.Builder chatBulider = ChatBean.newBuilder();
//////		chatBulider.setChatType(ChatType.TEXT);
//////		chatBulider.setContent("i love you yiqian");
//////		ChatMsg.Builder bulider = ChatMsg.newBuilder();
//////		
//////		bulider.setToken("admin");
//////		
//////		bulider.addChatPayloads(chatBulider.build());
//////		bulider.addChatPayloads(chatBulider.setChatType(ChatType.IMAGE).setIsAckNeed(false).build());
//////		
//////		
//////		
//////		ChatMsg msg = bulider.build();
//////		
//////		byte[] locMsgBytes = msg.toByteArray();
//////		System.out.println("the length is: " + locMsgBytes.length );
//////		
//////		ChatMsg msg2 = null;
//////		
//////		try {
//////			msg2 = ChatMsg.getDefaultInstance().parseFrom(locMsgBytes);
//////		} catch (InvalidProtocolBufferException e) {
//////			e.printStackTrace();
//////		}
////////		System.out.println(msg2.getChatPayloads(0).getIsAckNeed());
////////		System.out.println(msg2.getChatPayloads(1).getIsAckNeed());
////////		System.out.println(msg2.toString());
//////	}
//////	
//////	@Test 
//////	public void test1() throws ClassNotFoundException, Exception, IllegalAccessException {
////////		ChatBean.Builder chat = ChatBean.newBuilder();
////////		Class<?> a = Class.forName(ChatBean.Builder.class.getName());
//////////		Field[] fields = a.getDeclaredFields();
//////////		for( Field f : fields ) {
//////////			
//////////			f.setAccessible(true);
//////////			if(f.getName().equals("userIdSrc_")) {
//////////				f.set(chat, 1);
//////////				System.out.println(f.getName());
//////////			}
//////////			
//////////		}
////////		
//////////		Method method = a.getMethod("setContent",String.class);
//////////		method.invoke(chat, "i love yiqian");
////////		Field[] fields = a.getDeclaredFields();
////////		for( Field f : fields ) {
////////			
////////			f.setAccessible(true);
////////			if(f.getName().equals("userIdSrc_")) {
////////				f.set(chat, 1);
////////				System.out.println(f.getName());
////////			}
////////			
////////		}
////////		System.out.println(chat.getUserIdSrc());
////////		
////////		byte[] locMsgBytes = chat.build().toByteArray();
////////		System.out.println("the length is: " + locMsgBytes.length );
////////		
////////		ChatBean msg2 = null;
////////		
////////		try {
////////			msg2 = ChatBean.parseFrom(locMsgBytes);
////////		} catch (InvalidProtocolBufferException e) {
////////			e.printStackTrace();
////////		}
////////		System.out.println(msg2.toString());
////////		
////	}
////
//}
