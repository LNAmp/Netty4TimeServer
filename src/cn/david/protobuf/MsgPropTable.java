package cn.david.protobuf;

import java.util.HashMap;
import java.util.Map;

import cn.david.domain.AddressBookProtos.Person;
import cn.david.handler.PersonHandler;

public class MsgPropTable {
	
	private static Map<String,MsgProp> propTable = new HashMap<String,MsgProp>();
	static {
		MsgProp person = new MsgProp();
		person.setProtoType(Person.getDefaultInstance());
		person.setHandlerFullName(PersonHandler.class.getName());
		propTable.put("P", person);
	}
	
	public static MsgProp getMsgProp(String msgTypeName) {
		return propTable.get(msgTypeName);
	}

}
