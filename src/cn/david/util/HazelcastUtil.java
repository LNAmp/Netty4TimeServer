package cn.david.util;

import java.util.Set;

import com.hazelcast.core.Member;

import cn.david.factory.HazelcastFactory;

public final class HazelcastUtil {
	
	public static void init() {
		HazelcastFactory.getInstance();
	}
	
	public static String getServerUUID() {
		return HazelcastFactory.getInstance().getCluster().getLocalMember().getUuid();
	}
	
	public static Set<Member> getMembers() {
		return HazelcastFactory.getInstance().getCluster().getMembers();
	}
}
