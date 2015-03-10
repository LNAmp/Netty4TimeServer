package cn.david.task;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.david.connection.UserConnection;
import cn.david.connection.UserConnectionManager;
import cn.david.constants.ServerConstants;
import cn.david.db.BeanHandler;
import cn.david.db.DbcpUtil;
import cn.david.db.QueryHelper;
import cn.david.domain.AckCode;
import cn.david.domain.AckType;
import cn.david.domain.IMProto.AckMsg;
import cn.david.domain.IMProto.LoginMsg;
import cn.david.domain.User;
import cn.david.factory.HazelcastFactory;
import cn.david.handler.IMServerMsgHandler;
import cn.david.session.SessionManager;
import cn.david.session.UserSession;
import cn.david.util.HazelcastUtil;
import cn.david.util.Md5Util;
import cn.david.util.MsgUtil;
import cn.david.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class LoginTask implements Runnable {

	private ChannelHandlerContext ctx;
	private LoginMsg loginMsg;
	Logger logger = Logger.getLogger(LoginTask.class);

	public LoginTask(LoginMsg loginMsg, ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.loginMsg = loginMsg;
	}

	// 改方法面对的是过来的时候有userId的，如果没有userId则用下面那个方法
	@Override
	public void run() {
		logger.info("start run loginTask,congs!");
		// 判断是否是以邮箱作为用户名
		String username = loginMsg.getUsername();
		String password = Md5Util.encode(loginMsg.getPassword());
		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
		User user = null;
		// 根据userId查询session有没有该用户的登录信息
		UserSession userSession = SessionManager.getUserSession(loginMsg
				.getUserId());
		if (userSession == null) {
			if (username.contains("@")) {
				// 以邮箱作为用户名
				String sql = "select userId,username,password,email from user where email=? and password=?";
				try {
					user = (User) qh.query(sql, new Object[] { username,
							password }, new BeanHandler(User.class));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				String sql = "select userId,username,password,email from user where username=? and password=?";
				try {
					user = (User) qh.query(sql, new Object[] { username,
							password }, new BeanHandler(User.class));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (user == null) {
				// 验证失败
				logger.info("can't find the user!");
				AckMsg.Builder builder = AckMsg.newBuilder();
				builder.setAckType(AckType.LOGIN)
						.setResponseCode(AckCode.LOGIN_WRONG_UN_OR_PWD)
						.setMsgId(loginMsg.getMsgId());
				MsgUtil.sendAckMsg(ctx, builder.build());
			} else {
				logger.info("the user is " + user.toString());
				// 更新ctx中的attribute key user信息
				// ctx.attr(AttributeKey.valueOf("userInfo")).set(user);
				ctx.attr(IMServerMsgHandler.userInfo).set(user);
				// 更新userConnection map
				UserConnection userConn = new UserConnection();
				userConn.setCtx(ctx);
				UserConnectionManager.addUserConnection(user.getUserId(),
						userConn);
				// 需要考虑两种不同的情况，有可能是掉线了重连的，这个时候session中还存在在线信息
				String token = SessionUtil.generateToken(user.getUserId());
				// 已经不存在旧的session了
				// 建立新的session，新的token,存入token,user,ctx
				UserSession newSession = new UserSession(user.getUserId());
				
				newSession.setAttribute("user", user)
						.setAttribute("token", token)
						.setAttribute("serverId",HazelcastUtil.getServerUUID()).commit();
				// 发送登录成功
				AckMsg.Builder builder = AckMsg.newBuilder();
				builder.setAckType(AckType.LOGIN)
						.setResponseCode(AckCode.LOGIN_SUCCESS)
						.setMsgId(loginMsg.getMsgId()).setToken(token);
				MsgUtil.sendAckMsg(ctx, builder.build());
			}
		} else {
			//session中存在
			//验证用户名和密码
			user = (User) userSession.getAttribute("user");
			if(user == null) {
				//未知错误
				AckMsg.Builder builder = AckMsg.newBuilder();
				builder.setAckType(AckType.LOGIN)
						.setResponseCode(AckCode.LOGIN_UNKNOW_ERROR)
						.setMsgId(loginMsg.getMsgId());
				MsgUtil.sendAckMsg(ctx, builder.build());
			}
			String tempUsername = "";
			//用邮箱登录
			if(username.contains("@")) {
				//验证登录
				tempUsername = user.getEmail();
			} else {
				tempUsername = user.getUsername();
			}
			if(username.equals(tempUsername) && password.equals(user.getPassword())) {
				//登录成功
				logger.info("the user is " + user.toString());
				// 更新ctx中的attribute key user信息
				// ctx.attr(AttributeKey.valueOf("userInfo")).set(user);
				ctx.attr(IMServerMsgHandler.userInfo).set(user);
				// 更新userConnection map
				UserConnection userConn = new UserConnection();
				userConn.setCtx(ctx);
				UserConnectionManager.addUserConnection(user.getUserId(),
						userConn);
				// 需要考虑两种不同的情况，有可能是掉线了重连的，这个时候session中还存在在线信息
				String token = SessionUtil.generateToken(user.getUserId());
				userSession.setAttribute("token", token)
				 .setAttribute("serverId",HazelcastUtil.getServerUUID()).commit();
				
				// 发送登录成功
				AckMsg.Builder builder = AckMsg.newBuilder();
				builder.setAckType(AckType.LOGIN)
						.setResponseCode(AckCode.LOGIN_SUCCESS)
						.setMsgId(loginMsg.getMsgId()).setToken(token);
				MsgUtil.sendAckMsg(ctx, builder.build());
				
			} else {
				//登录失败
				logger.info("can't find the user!");
				AckMsg.Builder builder = AckMsg.newBuilder();
				builder.setAckType(AckType.LOGIN)
						.setResponseCode(AckCode.LOGIN_WRONG_UN_OR_PWD)
						.setMsgId(loginMsg.getMsgId());
				MsgUtil.sendAckMsg(ctx, builder.build());
			}
		}
	}

	// @Override
	// public void run() {
	// logger.info("start run loginTask,congs!");
	// //判断是否是以邮箱作为用户名
	// String username = loginMsg.getUsername();
	// String password = Md5Util.encode(loginMsg.getPassword());
	// QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
	// User user = null;
	// if(username.contains("@")) {
	// //以邮箱作为用户名
	// String sql =
	// "select userId,username,password,email from user where email=? and password=?";
	// try {
	// user = (User) qh.query(sql, new Object[]{username,password}, new
	// BeanHandler(User.class));
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// } else {
	// String sql =
	// "select userId,username,password,email from user where username=? and password=?";
	// try {
	// user = (User) qh.query(sql, new Object[]{username,password}, new
	// BeanHandler(User.class));
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(user == null) {
	// //验证失败
	// logger.info("can't find the user!");
	// AckMsg.Builder builder = AckMsg.newBuilder();
	// builder.setAckType(AckType.LOGIN)
	// .setResponseCode(AckCode.LOGIN_WRONG_UN_OR_PWD)
	// .setMsgId(loginMsg.getMsgId());
	// MsgUtil.sendAckMsg(ctx, builder.build());
	// } else {
	// logger.info("the user is " + user.toString());
	// //更新ctx中的attribute key user信息
	// //ctx.attr(AttributeKey.valueOf("userInfo")).set(user);
	// ctx.attr(IMServerMsgHandler.userInfo).set(user);
	// //更新userConnection map
	// UserConnection userConn = new UserConnection();
	// userConn.setCtx(ctx);
	// UserConnectionManager.addUserConnection(user.getUserId(), userConn);
	// //需要考虑两种不同的情况，有可能是掉线了重连的，这个时候session中还存在在线信息
	// UserSession userSession =
	// SessionManager.getUserSession(user.getUserId());
	// String token = SessionUtil.generateToken(user.getUserId());
	// if(userSession == null) {
	// //已经不存在旧的session了
	// //建立新的session，新的token,存入token,user,ctx
	// UserSession newSession = new UserSession(user.getUserId());
	// newSession.setAttribute("user", user)
	// .setAttribute("token", token)
	// .setAttribute("serverName", ServerConstants.serverName).commit();
	// } else {
	// //存在旧的session
	// //替换token,替换ctx
	// userSession.setAttribute("token", token)
	// .setAttribute("serverName", ServerConstants.serverName).commit();
	// }
	// //发动登录成功
	// AckMsg.Builder builder = AckMsg.newBuilder();
	// builder.setAckType(AckType.LOGIN)
	// .setResponseCode(AckCode.LOGIN_SUCCESS)
	// .setMsgId(loginMsg.getMsgId())
	// .setToken(token);
	// MsgUtil.sendAckMsg(ctx, builder.build());
	// }
	// }

}
