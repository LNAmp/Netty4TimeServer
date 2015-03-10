package cn.david.handler;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.log4j.Logger;

import cn.david.domain.MsgType;
import cn.david.domain.ServerMsg;
import cn.david.util.DateUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatCheckHandler extends ChannelInboundHandlerAdapter {

	Logger logger = Logger.getLogger(HeartbeatCheckHandler.class);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof String ) {
			//如果是客户端的回应，则不传回到后面的业务处理逻辑
			if(MsgType.PONG.equals( (String)msg )) {
				logger.info("At time : "+ DateUtil.printCurTime() +" ,received PONG from client.");
				return;
			}
		}
		//传递给后面的channelhandler
		ctx.fireChannelRead(msg);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		//如果空闲会触发不同的空闲事件
		if( evt instanceof IdleStateEvent) {
			IdleStateEvent idleEvt = (IdleStateEvent) evt;
			if(idleEvt.state() == IdleState.WRITER_IDLE) {
				logger.info("At time : "+ DateUtil.printCurTime() +" ,to send the PING.");
				ServerMsg msg = new ServerMsg();
				msg.setMsgType(MsgType.PING);
				msg.setProtoMsgContent(null);
//				List<String> names= ctx.pipeline().names();
//				for(String name : names ) {
//					System.out.println("channel hander :" + name);
//				}
				ctx.writeAndFlush(msg);
				return;
			} else if(idleEvt.state() == IdleState.READER_IDLE) {
				logger.info("At time : "+ DateUtil.printCurTime() +" timeout to rev the PONG. ");
				ctx.fireExceptionCaught(new SocketTimeoutException(
						"force to close channel, reason: time out."));
				//关闭此通道
				ctx.channel().close();
			}
		}
		super.userEventTriggered(ctx, evt);
	}
}
