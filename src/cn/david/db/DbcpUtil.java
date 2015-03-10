package cn.david.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;


public class DbcpUtil {
	
	private static DataSource ds;
	static {
		try {
			InputStream in = DbcpUtil.class.getClassLoader().getResourceAsStream("dbcpconfig.properties");
			Properties p = new Properties();
			p.load(in);
			ds = BasicDataSourceFactory.createDataSource(p);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataSource getDataSource(){
		return ds;
	}
	
	
	
}
