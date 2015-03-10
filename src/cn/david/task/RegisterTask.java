package cn.david.task;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import cn.david.db.DbcpUtil;
import cn.david.db.QueryHelper;
import cn.david.domain.AckCode;
import cn.david.domain.AckType;
import cn.david.domain.IMProto.RegisterMsg;
import cn.david.util.Md5Util;
import cn.david.util.MsgUtil;

public class RegisterTask implements Runnable {

	private RegisterMsg regMsg;
	private ChannelHandlerContext ctx;
	Logger logger = Logger.getLogger(RegisterTask.class);
	
	public RegisterTask(RegisterMsg regMsg, ChannelHandlerContext ctx) {
		this.regMsg = regMsg;
		this.ctx = ctx;
	}
	
	@Override
	public void run() {
		logger.info("start run regTask, congs!");
		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
		String sql = "insert into user (username,password,email) values (?,?,?)";
		int result = 0;
		String pwdEncoded = Md5Util.encode(regMsg.getPassword());
		try {
			result = qh.update(sql, new Object[]{regMsg.getUsername(),pwdEncoded,regMsg.getEmail()});
		} catch (SQLException e) {
			MsgUtil.sendAckMsg(ctx, AckType.REGISTER, AckCode.REGISTER_UNKNOW_ERROR, regMsg.getMsgId());
			return;
		}
		if(result == 0) {
			MsgUtil.sendAckMsg(ctx, AckType.REGISTER, AckCode.REGISTER_UNKNOW_ERROR, regMsg.getMsgId());
		} else {
			MsgUtil.sendAckMsg(ctx, AckType.REGISTER, AckCode.REGISTER_SUCCESS, regMsg.getMsgId());
		}
	}

}
