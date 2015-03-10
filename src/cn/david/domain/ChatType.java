package cn.david.domain;

public final class ChatType {
	//表示发送的内容为文本
	public static final int TEXT = 1;
	//表示发送的内容为图片链接
	public static final int IMAGE = 2;
	//表示发送的内容为收到聊天信息的回复，表示用户已经收到
	public static final int ACK = 3;
}
