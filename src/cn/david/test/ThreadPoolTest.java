package cn.david.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class ThreadPoolTest {
	
	@Test
	public void test1() {
		ExecutorService es = Executors.newCachedThreadPool();
		
	}
}
