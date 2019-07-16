package com.quanyu.cgm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.quanyu.cgm.exception.CodegenException;

/**
 * ��jdbc���з�װʵ�����ݿ���ʡ�<br>
 * ����ģ��ģʽʵ�֡�

 *
 */
public class DaoHelper<T> {
	
	private String url="";
	private String userName="";
	private String pwd="";
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	/**
	 * �������ݿ�url���û��������롣
	 * @param url
	 * @param username
	 * @param password
	 */
	public DaoHelper(String url,String username,String password)
	{
		this.url=url;
		this.userName=username;
		this.pwd=password;
	}
	
	/**
	 * ��ѯһ������<br/>
	 * ʹ�÷�����<br/>
	 * <pre>
	 *   DaoHelper<String> dao=new DaoHelper<String>(this.url, this.username, this.password);
	 *		String sql=String.format(sqlTableComment, tableName);
	 *		String comment=dao.queryForObject(sql,new MapCmd<String>() {
	 *			public String getObjecFromRs(ResultSet rs) throws SQLException {
	 *				return rs.getString("comment");
	 *			}
	 *		});
	 *</pre>
	 * @param <T>
	 * 
	 * @param sql
	 * @param cmd
	 * @return
	 * @throws CodegenException
	 */
	public T queryForObject(String sql,MapCmd<T> cmd) throws CodegenException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = DriverManager.getConnection(this.url, this.userName, this.pwd);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return cmd.getObjecFromRs(rs);
			}
			System.out.println("û�е�����:" + sql);
			return null;
		} catch (SQLException e) {
			throw new CodegenException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new CodegenException(e);
			}
		}
		
	}

	/**
	 * ��ѯ�б����
	 * ʹ�÷������£�
	 * <pre>
	 * 		DaoHelper<ColumnModel> dao=new DaoHelper<ColumnModel>(this.url, this.username, this.password);
	 *		String sql=String.format(sqlColumn,tableName);
	 *		List<ColumnModel> list=dao.queryForList(sql, new OracleMapCmd());
	 *</pre>
	 * @param sql
	 * @param cmd
	 * @return
	 * @throws CodegenException
	 */
	public List<T> queryForList(String sql,MapCmd<T> cmd) throws CodegenException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<T> list=new ArrayList<T>();
		try {
			conn = DriverManager.getConnection(this.url, this.userName, this.pwd);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(cmd.getObjecFromRs(rs));
			}
		} catch (SQLException e) {
			throw new CodegenException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new CodegenException(e);
			}
		}
		return list;
	}

}
