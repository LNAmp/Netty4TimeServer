package cn.david.protobuf;

import java.util.HashMap;
import java.util.Map;

import cn.david.domain.IMProto;
import cn.david.domain.IMProto.AckMsg;
import cn.david.domain.IMProto.AskLocAckMsg;
import cn.david.domain.IMProto.AskLocMsg;
import cn.david.domain.IMProto.AskOfflineMsg;
import cn.david.domain.IMProto.ChatAckMsg;
import cn.david.domain.IMProto.ChatMsg;
import cn.david.domain.IMProto.LoginMsg;
import cn.david.domain.IMProto.RegisterMsg;
import cn.david.domain.IMProto.UploadLocMsg;
import cn.david.domain.MsgType;

import com.google.protobuf.MessageLite;

public class MsgTypeTable {
	private static Map<String, MessageLite> typeTable = new HashMap<String, MessageLite>();
	
	static {
		if(typeTable == null) {
			typeTable = new HashMap<String, MessageLite>();
		}
		typeTable.put(MsgType.UPLOAD_LOCATION, UploadLocMsg.getDefaultInstance());
		typeTable.put(MsgType.SEND_MESSAGE, IMProto.ChatMsg.getDefaultInstance());
		typeTable.put(MsgType.ASK_LOCATION, AskLocMsg.getDefaultInstance());
		typeTable.put(MsgType.ASK_LOCATION_ACK, AskLocAckMsg.getDefaultInstance());
		typeTable.put(MsgType.REGISTER, RegisterMsg.getDefaultInstance());
		typeTable.put(MsgType.LOGIN, LoginMsg.getDefaultInstance());
		typeTable.put(MsgType.ACK, AckMsg.getDefaultInstance());
		typeTable.put(MsgType.CHAT_ACK, ChatAckMsg.getDefaultInstance());
		typeTable.put(MsgType.CHAT, ChatMsg.getDefaultInstance());
		typeTable.put(MsgType.ASK_OFFLINEMSG, AskOfflineMsg.getDefaultInstance());
	}
	
	public static MessageLite getMsgProto(String msgType) {
		return typeTable.get(msgType);
	}
}
