package cn.david.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

public final class ExecutorServicelFactory {
	
	private static final ExecutorService es = Executors.newCachedThreadPool();
	private static IExecutorService ies = null;
	
	private ExecutorServicelFactory() {
	}
	
	public static ExecutorService getInstance() {
		return es;
	}
	
	public static IExecutorService getIExecutorService() {
		HazelcastInstance instance = HazelcastFactory.getInstance();
		if(ies == null) {
			synchronized (ExecutorServicelFactory.class) {
				if(ies == null) {
					ies = instance.getExecutorService("exec");
				}
			}
		}
		return ies;
	}
}
