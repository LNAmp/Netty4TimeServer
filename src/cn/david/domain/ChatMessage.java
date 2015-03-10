package cn.david.domain;

import java.io.Serializable;

import cn.david.domain.IMProto.ChatMsg;

public class ChatMessage implements Serializable {
	private int userIdSrc;
	private int userIdDesc;
	private String msgId;
	private int chatType;
	private String content;
	private long sendTime;
	public int getUserIdSrc() {
		return userIdSrc;
	}
	public void setUserIdSrc(int userIdSrc) {
		this.userIdSrc = userIdSrc;
	}
	public int getUserIdDesc() {
		return userIdDesc;
	}
	public void setUserIdDesc(int userIdDesc) {
		this.userIdDesc = userIdDesc;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public int getChatType() {
		return chatType;
	}
	public void setChatType(int chatType) {
		this.chatType = chatType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	
	public ChatMessage populateChatMessage(ChatMsg msg) {
		this.userIdSrc = msg.getUserIdSrc();
		this.userIdDesc = msg.getUserIdDesc();
		this.msgId = msg.getMsgId();
		this.chatType = msg.getChatType();
		this.content = msg.getContent();
		this.sendTime = msg.getSendTime();
		return this;
	}
	
	public ChatMsg toChatMsg() {
		ChatMsg.Builder builder = ChatMsg.newBuilder();
		return builder.setUserIdSrc(this.userIdSrc)
			.setUserIdDesc(this.userIdDesc)
			.setMsgId(this.msgId)
			.setChatType(this.chatType)
			.setContent(this.content)
			.setSendTime(this.sendTime).build();
	}
	@Override
	public String toString() {
		return "ChatMessage [userIdSrc=" + userIdSrc + ", userIdDesc="
				+ userIdDesc + ", msgId=" + msgId + ", chatType=" + chatType
				+ ", content=" + content + ", sendTime=" + sendTime + "]";
	}

	
	
	
}
