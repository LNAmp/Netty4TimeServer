package cn.david.handler;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.log4j.Logger;

import cn.david.connection.UserConnection;
import cn.david.connection.UserConnectionManager;
import cn.david.constants.IdleStateConstant;
import cn.david.domain.AckCode;
import cn.david.domain.AckType;
import cn.david.domain.ChatAckCode;
import cn.david.domain.ChatType;
import cn.david.domain.IMProto.AskLocMsg;
import cn.david.domain.IMProto.AskOfflineMsg;
import cn.david.domain.IMProto.ChatMsg;
import cn.david.domain.IMProto.LoginMsg;
import cn.david.domain.IMProto.RegisterMsg;
import cn.david.domain.IMProto.UploadLocMsg;
import cn.david.domain.ChatMessage;
import cn.david.domain.MsgType;
import cn.david.domain.ServerMsg;
import cn.david.domain.User;
import cn.david.factory.ExecutorServicelFactory;
import cn.david.session.SessionManager;
import cn.david.session.UserSession;
import cn.david.task.AskLocTask;
import cn.david.task.ChatMsgRouteTask;
import cn.david.task.LoginTask;
import cn.david.task.RegisterTask;
import cn.david.task.UploadLocTask;
import cn.david.util.DateUtil;
import cn.david.util.HazelcastUtil;
import cn.david.util.MsgUtil;
import cn.david.util.RedisUtil;

import com.google.protobuf.MessageLite;
import com.hazelcast.core.Member;

public class IMServerMsgHandler extends ChannelInboundHandlerAdapter {
	
	Logger logger = Logger.getLogger(IMServerMsgHandler.class);

	public static final AttributeKey<User> userInfo = AttributeKey.valueOf("userInfo");
	//private static int pongCount = 5;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.info("the ServerMsgHandler start to handler Msg.");
		if(msg instanceof String) {
			if(MsgType.PING.equals((String)msg)) {
				logger.info("At time : "+ DateUtil.printCurTime() +" ,recevie the PING.");
//				if(pongCount == 5) {
//					return;
//				}
				ServerMsg msg1 = new ServerMsg();
				msg1.setMsgType(MsgType.PONG);
				msg1.setProtoMsgContent(null);
//				pongCount++;
				ctx.writeAndFlush(msg1);
				return;
			}else if(MsgType.PONG.equals((String)msg)) {
				System.out.println(msg);
			}
		}else if(msg instanceof UploadLocMsg) {
			logger.info("process the UploadLocMsg");
			processUploadLocMsg( (UploadLocMsg)msg ,ctx);
		} else if(msg instanceof AskLocMsg) {
			logger.info("process the AskLocMsg");
			processAskLocMsg( (AskLocMsg)msg, ctx);
		}else if(msg instanceof RegisterMsg ) {
			logger.info("process the RegisterMsg.");
			processRegisterMsg( (RegisterMsg)msg ,ctx);
		} else if(msg instanceof LoginMsg) {
			logger.info("process the LoginMsg.");
			pocessLoginMsg( (LoginMsg)msg, ctx);
		} else if(msg instanceof ChatMsg) {
			logger.info("process the ChatMsg.");
			processChatMsg((ChatMsg)msg,ctx);
		} else if(msg instanceof AskOfflineMsg) {
			logger.info("process the AskOfflineMsg");
			processAskOfflineMsg((AskOfflineMsg)msg,ctx);
		}
	}
	
	/**
	 * 用户A向服务器GET用户B的地址
	 * @param msg
	 * @param ctx
	 */
	private void processAskLocMsg(AskLocMsg msg, ChannelHandlerContext ctx) {
		//对于要求定位信息的处理
		//首先还是要验证token
		//如果验证成功，则将其封装成task扔到线程池中
		String msgId = msg.getMsgId();
		int userId = msg.getUserIdSrc();
		String token = msg.getToken();
		tokenValidate(ctx, msgId, userId, token);
		AskLocTask askLocTask = new AskLocTask(msg, ctx);
		ExecutorServicelFactory.getInstance().submit(askLocTask);
	}

	private void processUploadLocMsg(UploadLocMsg msg, ChannelHandlerContext ctx) {
		//对于上传位置的处理
		//首先需要验证token
		//如果验证过了则将数据封装成task插入到数据库中
		String msgId = msg.getMsgId();
		int userId = msg.getUserId();
		String token = msg.getToken();
		tokenValidate(ctx, msgId, userId, token);
		UploadLocTask uploadLocTask = new UploadLocTask(msg, ctx);
		ExecutorServicelFactory.getInstance().submit(uploadLocTask);
	}

	/**
	 * 对于离线消息的处理，采用客户端主动拉的方式
	 * @param msg
	 * @param ctx
	 */
	private void processAskOfflineMsg(AskOfflineMsg msg,
			ChannelHandlerContext ctx) {
		//先验证token等，然后chatack，回复服务器收到了，然后将所有信息推过去
		String msgId = msg.getMsgId();
		int userId = msg.getUserId();
		String token = msg.getToken();
		tokenValidate(ctx, msgId, userId, token);
		MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_RECEIVED, msgId);
		List<ChatMessage> msgs = RedisUtil.getAllOfflineMsg(userId);
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.setMsgType(MsgType.CHAT);
		for(ChatMessage chatMsg : msgs) {
			serverMsg.setProtoMsgContent(chatMsg.toChatMsg());
			ctx.writeAndFlush(serverMsg);
		}
	}


	/**
	 * 处理聊天信息
	 * @param msg
	 * @param ctx
	 */
	private void processChatMsg(ChatMsg msg, ChannelHandlerContext ctx) {
		//首先检查是否登录
		String msgId = msg.getMsgId();
		int userId = msg.getUserIdSrc();
		String token = msg.getToken();
		//验证token
		tokenValidate(ctx, msgId, userId, token);
		
		int userBId = msg.getUserIdDesc();
		//查看对方是否登录
		UserSession sessionDesc = SessionManager.getUserSession(userBId);
		if(sessionDesc != null && (sessionDesc.getAttribute("waitConnent") == null) ) {
			//登录了，而且在线
			//查看接收方session是否在本机上，如果不在本机上，找到那台机器，路由一发
			if(UserConnectionManager.containUserConn(msg.getUserIdDesc())) {
				//在本机上
				//找到ctx，将该信息转发出去，将token拿掉其他可以不变
				//发送的消息暂时没有时间，因为是在线互发，所以没有时间应该也没有关系
				//时间还是加上，用不用再说，传秒级时间吧
				UserConnection bConn = UserConnectionManager.getUserConnection(userBId);
				ChannelHandlerContext ctxB = bConn.getCtx();
				ChatMsg.Builder builder = msg.toBuilder();
				ChatMsg msgB = builder.setToken("").build();
				ServerMsg bMsg = new ServerMsg();
				bMsg.setMsgType(MsgType.CHAT);
				bMsg.setProtoMsgContent(msgB);
				//为啥要保存ctx，因为ctx是线程安全的
				ctxB.writeAndFlush(bMsg);
				//这里暂缺一点东西，缺了服务器给客户端的“收到”回执
				//回写chat ack，表示服务器已经处理该信息
				if(msgB.getChatType()!=ChatType.ACK) {
					MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_RECEIVED, msg.getMsgId());
				}
				return;
			} else {
				//server to server
				//得到members
				//根据session判断哪个member
				String serverId = (String) sessionDesc.getAttribute("serverId");
				Set<Member> members = HazelcastUtil.getMembers();
				for(Member member : members) {
					//中了
					if(member.getUuid().equals(serverId)) {
						ChatMessage chatMsg = new ChatMessage();
						ExecutorServicelFactory.getIExecutorService()
							.executeOnMember(new ChatMsgRouteTask(chatMsg.populateChatMessage(msg)), member);
						return;
					}
				}
				//如果都没中，应该是服务器错误
				if(msg.getChatType()!=ChatType.ACK) {
					MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msg.getMsgId());

				}
							}
		} else {
			//离线或者是刚刚在掉线等待重连
			//要将信息保存到redis中,这里要更新时间，得到score，防止发送方作假时间
			//将token处理掉
			ChatMsg.Builder builder = msg.toBuilder();
			ChatMsg msgB = builder.setToken("").build();
			//由protobuf 封装成业务bean
			ChatMessage chatMsg = new ChatMessage();
			chatMsg = chatMsg.populateChatMessage(msg);
			RedisUtil.insertOfflineMsg(userBId, chatMsg);
			//回写chat ack
			if(msg.getChatType()!=ChatType.ACK) {
				MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_RECEIVED, msg.getMsgId());
			}
			return;
		}

	}


	private void tokenValidate(ChannelHandlerContext ctx, String msgId,
			int userId, String token) {
		User user = ctx.attr(IMServerMsgHandler.userInfo).get();
		if(user == null) {
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.LOGIN_FIRST, msgId);
			return;
		}
		if(user.getUserId() != userId) {
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.WRONG_UID, msgId);
			return;
		}
		//验证token信息
		if(! validateEmpty(token)) {
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.WRONG_TOKEN, msgId);
			return;
		}
		
		UserSession session = SessionManager.getUserSession(user.getUserId());
		if(session == null) {
			logger.info("UserId : " + user.getUserId()+",the session is null");
		}
		if(! token.equals((String)session.getAttribute("token"))) {
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.WRONG_TOKEN, msgId);
			return;
		}
	}


	/**
	 * 处理登录信息，先检查登录信息的格式，如果验证通过封装成LoginTask投到业务池中
	 * @param msg
	 * @param ctx
	 */
	private void pocessLoginMsg(LoginMsg msg, ChannelHandlerContext ctx) {
		//仅判断为空就行
		String username = msg.getUsername();
		String password = msg.getPassword();
		if(validateEmpty(username) && validateEmpty(password)) {
			//不空
			LoginTask loginTask = new LoginTask(msg, ctx);
			ExecutorServicelFactory.getInstance().submit(loginTask);
		} else {
			//空
			MsgUtil.sendAckMsg(ctx, AckType.LOGIN, AckCode.LOGIN_FIELD_EMPTY, msg.getMsgId());
		}
	} 


	/**
	 * 处理注册信息，先验证注册信息的合法性，如果验证通过则封装成RegisterTask投入到业务池中
	 * @param msg2
	 * @param ctx
	 */
	private void processRegisterMsg(RegisterMsg msg2, ChannelHandlerContext ctx) {
		//测试格式合法性
		//如果不合法直接返回相应的错误，如果合法需封装成task放入任务线程池中执行
		String username = msg2.getUsername();
		String password = msg2.getPassword();
		String email = msg2.getEmail();
		String msgId = msg2.getMsgId();
		if(validateEmpty(username) && validateEmpty(password) && validateEmpty(email)) {
			//通过第一步验证
			if(validateFormat(username, password, email)) {
				//两步都通过,放入业务线程中执行
				//MsgUtil.sendAckMsg(ctx, AckType.REGISTER,AckCode.REGISTER_SUCCESS, msgId);
				RegisterTask regTask = new RegisterTask(msg2, ctx);
				ExecutorServicelFactory.getInstance().submit(regTask);
			} else {
				//格式错误
				MsgUtil.sendAckMsg(ctx, AckType.REGISTER,AckCode.REGISTER_WRONG_FORMAT, msgId);
			}
		} else {
			//必填信息中有空信息
			MsgUtil.sendAckMsg(ctx, AckType.REGISTER,AckCode.REGISTER_FIELD_EMPTY, msgId);
		}
	}
	
	
	/**
	 * 验证用户名、密码和邮箱的格式合法性
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */
	private boolean validateFormat(String username,String password,String email) {
		if(!username.matches("[a-zA-Z0-9_]{2,14}")) {
			return false;
		}
		if(!(password.matches("[a-zA-Z0-9_]{6,16}"))) {
			return false;
		}
		if(! email.matches("^\\w+@\\w+(\\.[A-Za-z]+)+$")) {
			return false;
		}
		return true;
	}


	/**
	 * 验证是否为空，如果不为空返回true
	 * @param str
	 * @return
	 */
	private boolean validateEmpty(String str) {
		if(str == null) {
			return false;
		}
		if("".equals(str) || str.trim().equals("")) {
			return false;
		}
		return true;
	}



	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//当通道被激活的时候回被调用一次
		//在这个地方将心跳handler加入
		logger.info("the channelActive in IMServerMsgHandler is called");

//		ChannelPipeline pipeline = ctx.pipeline();
//		pipeline.addAfter("protobufEncoder", "idleStateHandler", new IdleStateHandler(
//				IdleStateConstant.READER_IDLE_TIME, IdleStateConstant.WRITER_IDLE_TIME, IdleStateConstant.ALL_IDLE_TIME));
//		pipeline.addAfter("idleStateHandler", "HeartbeatCheckHandler", new HeartbeatCheckHandler());
		
//		List<String> names= ctx.pipeline().names();
//		for(String name : names ) {
//			System.out.println("channel hander :" + name);
//		}
		super.channelActive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.info("the exceptionCaught in IMServerMsgHandler is called");
		if(cause instanceof SocketTimeoutException) {
			User user = ctx.attr(userInfo).get();
			//让session保存5分钟后失效
			if(user != null) {
				logger.info("the user " + user + "is about to leave.");
				UserSession session = SessionManager.getUserSession(user.getUserId());
				//表示正在等待连接，此时过来的信息直接去offline MSG
				session.setAttribute("waitConnent", true);
				session.invalidate(5);
				//清楚userConnection
				UserConnectionManager.removeUserConnection(user.getUserId());
			}
			//清除user信息
			ctx.attr(userInfo).set(null);
			ChannelFuture closeFuture = ctx.channel().close();
			closeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future)
						throws Exception {
					System.out.println("the channel is closed ,reason:TIME_OUT.");
				}
			});
		}
		//得处理其他IOException
		//还有就是客户端主动关闭会产生什么样的情况
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("the channelRegistered in IMServerMsgHandler is called");
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("the channelUnregistered in IMServerMsgHandler is called");
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("the channelActive in IMServerMsgHandler is called");
		super.channelInactive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("the channelReadComplete in IMServerMsgHandler is called");
		super.channelReadComplete(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		logger.info("the userEventTriggered in IMServerMsgHandler is called");
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		logger.info("the channelWritabilityChanged in IMServerMsgHandler is called");
		super.channelWritabilityChanged(ctx);
	}
}
