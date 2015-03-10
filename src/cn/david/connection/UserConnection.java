package cn.david.connection;

import io.netty.channel.ChannelHandlerContext;

public class UserConnection {

	private ChannelHandlerContext ctx;

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	
}
