package cn.david.task;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.david.db.DbcpUtil;
import cn.david.db.ProtobufBeanHandler;
import cn.david.db.QueryHelper;
import cn.david.domain.ChatAckCode;
import cn.david.domain.IMProto.AskLocAckMsg;
import cn.david.domain.IMProto.AskLocMsg;
import cn.david.util.MsgUtil;
import io.netty.channel.ChannelHandlerContext;

public class AskLocTask implements Runnable {

	private ChannelHandlerContext ctx;
	private AskLocMsg msg;
	
	Logger logger = Logger.getLogger(AskLocTask.class);
	
	public AskLocTask(AskLocMsg msg,ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.msg = msg;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("start run AskLocTask");
		//此处还需要权限验证等工作，需要有一张授权表
		//暂时空缺
		//从数据库中查出最近的一个位置
		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
		String sql = "select longitude,latitude,mapId,floor,locationType,updateTime " +
				"from location_upload where userId=? and locationType=? order by updateTime DESC";
		AskLocAckMsg locMsg = null;
		try {
			locMsg = (AskLocAckMsg) qh.query(sql, new Object[] {msg.getUserIdDesc(),msg.getLocationType()},
					new ProtobufBeanHandler(AskLocAckMsg.newBuilder(), AskLocAckMsg.Builder.class));
		} catch (SQLException e) {
			//只能返回服务器错误了
			//客户端能同步读写的只能是AckMsg
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msg.getMsgId());
			e.printStackTrace();
		}
		//如果没有得到定位结果
		if(locMsg == null) {
			//给客户端发送ChatAckMsg，并不会转成AckResponse
			//给客户端发送服务器错误
			MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_ERROR, msg.getMsgId());
			return;
		}
		//如果已经得到定位结果
		//则发送“服务器已经收到”
		MsgUtil.sendChatAckMsg(ctx, ChatAckCode.SERVER_RECEIVED, msg.getMsgId());
		
		//发送定位结果，任务完成
		AskLocAckMsg.Builder locBuilder = locMsg.toBuilder();
		locMsg = locBuilder.setUserIdSrc(msg.getUserIdSrc())
			.setUserIdDesc(msg.getUserIdDesc())
			.setToken(msg.getToken())
			.setMsgId(msg.getMsgId()).build();
		MsgUtil.sendLocAckMsg(ctx, locMsg);
	}
	
}
