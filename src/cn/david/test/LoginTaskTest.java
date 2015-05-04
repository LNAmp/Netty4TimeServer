package cn.david.test;

import java.sql.SQLException;

import org.junit.Test;

import cn.david.db.DbcpUtil;
import cn.david.db.QueryHelper;
import cn.david.domain.AckCode;
import cn.david.domain.AckType;
import cn.david.domain.IMProto.LoginMsg;
import cn.david.task.LoginTask;
import cn.david.util.Md5Util;
import cn.david.util.MsgUtil;

public class LoginTaskTest {
	
	@Test
	public void test1() {
		LoginMsg.Builder builder = LoginMsg.newBuilder();
		builder.setUsername("aaaaaa@a.com")
			.setPassword("qinqinhaiou");
		LoginTask task = new LoginTask(builder.build(), null);
		task.run();
	}
	
	@Test
	public void registerAccount() {
		QueryHelper qh = new QueryHelper(DbcpUtil.getDataSource());
		for(int i=1000;i<5000;i++) {
			String username = "david" + i;
			String password = "qinqinhaiou";
			String sql = "insert into user (username,password,email) values (?,?,?)";
			String pwdEncoded = Md5Util.encode(password);
			try {
				qh.update(sql, new Object[]{username,pwdEncoded,"aaab@aa.com"});
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
