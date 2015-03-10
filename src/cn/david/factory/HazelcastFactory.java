package cn.david.factory;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

//由于这个的特殊性，只能用二次判断
public class HazelcastFactory {
	private static volatile HazelcastInstance instance = null;
		
	public static HazelcastInstance getInstance() {
		if(instance == null) {
			synchronized (HazelcastFactory.class) {
				if(instance == null) {
					Config config = new ClasspathXmlConfig("hazelcast.xml");
					instance = Hazelcast.newHazelcastInstance(config);
				}
			}
		}
		return instance;
	}
}
