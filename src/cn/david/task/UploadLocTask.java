package cn.david.task;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.david.db.DbcpUtil;
import cn.david.db.QueryHelper;
import cn.david.domain.ChatAckCode;
import cn.david.domain.IMProto.UploadLocMsg;
import cn.david.util.MsgUtil;
import io.netty.channel.ChannelHandlerContext;

public class UploadLocTask implements Runnable {

	private ChannelHandlerContext ctx;
	private UploadLocMsg msg;
	
	Logger logger = Logger.getLogger(UploadLocTask.class);
	
	public UploadLocTask(UploadLocMsg uploadLocMsg, ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.msg = uploadLocMsg;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("start run UploadLocTask!");
		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
		String sql = "insert into location_upload (userId,longitude,latitude,mapId,floor,locationType,updateTime) " +
				"values (?,?,?,?,?,?,?) ";
		int result = 0;
		try {
			result = qh.update(sql, new Object[] {msg.getUserId(),msg.getLongitude(),msg.getLatitude(),msg.getMapId(),msg.getFloor(),
					msg.getLocationType(),msg.getUpdateTime()});
		} catch (SQLException e) {
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msg.getMsgId());
			return;
		}
		
		if(result == 0) {
			//插入失败
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msg.getMsgId());
			return;
		} else {
			//成功，表示服务器已经接收
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_RECEIVED, msg.getMsgId());
			return;
		}
	}

}
