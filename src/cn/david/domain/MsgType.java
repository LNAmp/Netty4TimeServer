package cn.david.domain;

public final class MsgType {
	public static final String UPLOAD_LOCATION = "UL";
	public static final String SEND_MESSAGE = "SM";
	public static final String ASK_LOCATION = "AL";
	public static final String ASK_LOCATION_ACK = "LA";
	public static final String REGISTER ="RG";
	public static final String LOGIN = "LG";
	public static final String PING = "PI";
	public static final String PONG = "PO";
	public static final String ACK = "AC";
	//ACK_MSG是对于登录和注册的ACK
	//CHAT_ACK对发送对话消息的ACK
	//对话消息主要包括聊天、上传和获取位置、获取离线消息等
	public static final String CHAT_ACK = "CA";
	public static final String CHAT = "CH";
	public static final String ASK_OFFLINEMSG = "AO";
}
