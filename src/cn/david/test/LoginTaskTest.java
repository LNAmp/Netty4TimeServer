package cn.david.test;

import org.junit.Test;

import cn.david.domain.IMProto.LoginMsg;
import cn.david.task.LoginTask;

public class LoginTaskTest {
	
	@Test
	public void test1() {
		LoginMsg.Builder builder = LoginMsg.newBuilder();
		builder.setUsername("aaaaaa@a.com")
			.setPassword("qinqinhaiou");
		LoginTask task = new LoginTask(builder.build(), null);
		task.run();
	}
}
