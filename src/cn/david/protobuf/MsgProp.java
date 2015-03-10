package cn.david.protobuf;

import com.google.protobuf.MessageLite;

public class MsgProp {
	private MessageLite protoType;
	private String handlerFullName;
	public MessageLite getProtoType() {
		return protoType;
	}
	public void setProtoType(MessageLite protoType) {
		this.protoType = protoType;
	}
	public String getHandlerFullName() {
		return handlerFullName;
	}
	public void setHandlerFullName(String handlerFullName) {
		this.handlerFullName = handlerFullName;
	}
	
	
	
}
