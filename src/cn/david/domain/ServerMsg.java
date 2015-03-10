package cn.david.domain;

import com.google.protobuf.MessageLite;

public class ServerMsg {
	private String msgType;
	private MessageLite protoMsgContent;
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public MessageLite getProtoMsgContent() {
		return protoMsgContent;
	}
	public void setProtoMsgContent(MessageLite protoMsgContent) {
		this.protoMsgContent = protoMsgContent;
	}
	
	
}
