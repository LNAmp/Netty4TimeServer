package cn.david.db;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class QueryHelper {
	private DataSource ds;
	
	public QueryHelper(DataSource ds) {
		this.ds = ds;
	}
	
	public int update(String sql, Object[] params ) throws SQLException {
		Connection conn = ds.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ParameterMetaData metaData = pstmt.getParameterMetaData();
		int count = metaData.getParameterCount();
		if(count < 0 || (count > 0 && params == null)) {
			throw new IllegalArgumentException("the params can't be null");
		}
		if(count != params.length) {
			throw new IllegalArgumentException("not enough params ");
		}
		for(int i = 0; i< count ; i++) {
			pstmt.setObject(i+1, params[i] );
		}
		int result = pstmt.executeUpdate();
		release(conn, pstmt, null) ;
		return result;
	}
	
	public Object query(String sql, Object[] params, ResultSetHandler rsHandler) throws SQLException {
		Connection conn = ds.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ParameterMetaData metaData = pstmt.getParameterMetaData();
		int count = metaData.getParameterCount();
		if(count < 0 || (count > 0 && params == null)) {
			throw new IllegalArgumentException("the params can't be null");
		}
		if(count != params.length) {
			throw new IllegalArgumentException("not enough params ");
		}
		for(int i = 0; i< count ; i++) {
			pstmt.setObject(i+1, params[i] );
		}
		
		ResultSet rs = pstmt.executeQuery();
		Object result = rsHandler.handleResultSet(rs);
		release(conn, pstmt, rs) ;
		return result;
	}

	private void release(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
