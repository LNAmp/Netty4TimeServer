package cn.david.db;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class BeanHandler implements ResultSetHandler {

	private Class<?> clazz = null;
	
	public BeanHandler(Class<?> clazz) {
		this.clazz = clazz;
	}
	@Override
	public Object handleResultSet(ResultSet rs) throws SQLException {
		Object bean = null;
		if(rs.next()) {
			try {
				bean = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 0; i < metaData.getColumnCount(); i++) {
				int type = metaData.getColumnType(i+1);
				String name = metaData.getColumnName(i+1);
				Object value = rs.getObject(i+1);
				Field f = null;
				try {
					f = clazz.getDeclaredField(name);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				f.setAccessible(true);
				try {
					f.set(bean, value);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
}
