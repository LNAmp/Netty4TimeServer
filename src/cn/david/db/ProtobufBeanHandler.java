package cn.david.db;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;

public class ProtobufBeanHandler implements ResultSetHandler {

	MessageLite.Builder builder = null;
	Class<?> clazz = null;
	
	public ProtobufBeanHandler(MessageLite.Builder builder,Class<?> builderClass) {
		this.builder = builder;
		this.clazz = builderClass;
	}
	@Override
	public Object handleResultSet(ResultSet rs) throws SQLException {
		if(rs.next()) {
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 0; i < metaData.getColumnCount(); i++) {
				int type = metaData.getColumnType(i+1);
				String name = metaData.getColumnName(i+1);
				Object value = rs.getObject(i+1);
				process(builder,clazz, type, name,value);
			}
		}
		return builder.build();
	}
	private void process(Builder b, Class<?> clazz, int type, String name,Object value) {
		Class<?> c = null;
		if(type == Types.BIGINT) {
			c = long.class;
		} else if(type == Types.TINYINT) {
			c = boolean.class;
		} else if(type == Types.VARCHAR) {
			c = String.class;
		} else if(type == Types.INTEGER) {
			c = int.class;
		} else if(type == Types.DOUBLE) {
			c = double.class;
		}
		if( c == null) {
			return;
		}
		String a = name.substring(0, 1).toUpperCase();
		String setMethodName = "set" + a + name.substring(1);
		try {
			Method method = clazz.getMethod(setMethodName, c);
			method.invoke(b, value);
		} catch (Exception e) {
			
		}
	}
	
	
}
