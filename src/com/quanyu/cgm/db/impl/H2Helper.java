package com.quanyu.cgm.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.quanyu.cgm.db.DaoHelper;
import com.quanyu.cgm.db.IDbHelper;
import com.quanyu.cgm.db.MapCmd;
import com.quanyu.cgm.exception.CodegenException;
import com.quanyu.cgm.model.ColumnModel;
import com.quanyu.cgm.model.TableModel;
import com.quanyu.cgm.util.StringUtil;

/**
 * ȡ�����ݿ��ӿ�IDbHelper��H2 ��ʵ��
 *
 */
public class H2Helper implements IDbHelper {
	// ȡ��ע��
	private String sqlTableComment = 
			"SELECT "+
				"TABLE_NAME, "+
				"REMARKS "+
			"FROM "+
				"INFORMATION_SCHEMA.TABLES T "+
			"WHERE "+
				"T.TABLE_SCHEMA=SCHEMA() "+
				"AND UPPER(TABLE_NAME) = UPPER('%s') ";

	// ȡ���б�
	private String sqlColumn = 
			"SELECT "+
					"A.TABLE_NAME, "+
					"A.COLUMN_NAME, "+
					"A.IS_NULLABLE, "+
					"A.TYPE_NAME, "+
					"A.CHARACTER_OCTET_LENGTH LENGTH, "+
					"A.NUMERIC_PRECISION PRECISIONS, "+
					"A.NUMERIC_SCALE SCALE, "+
					"B.COLUMN_LIST, "+
					"A.REMARKS "+ 
				"FROM "+
					"INFORMATION_SCHEMA.COLUMNS A  "+
					"JOIN INFORMATION_SCHEMA.CONSTRAINTS B ON A.TABLE_NAME=B.TABLE_NAME "+
				"WHERE  "+
					"A.TABLE_SCHEMA=SCHEMA() "+ 
					"AND B.CONSTRAINT_TYPE='PRIMARY KEY' "+
					"AND UPPER(A.TABLE_NAME) = UPPER('%s') ";
			
	// ȡ�����ݿ����б�
	private String sqlAllTables =
			"SELECT "+
					"TABLE_NAME, "+
					"REMARKS "+
				"FROM "+
					"INFORMATION_SCHEMA.TABLES T "+
				"WHERE "+
					"T.TABLE_SCHEMA=SCHEMA() ";

	private String url;
	private String username;
	private String password;

	public H2Helper() throws CodegenException {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new CodegenException("�Ҳ���H2����!", e);
		}
	}

	public void setUrl(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * ���ݱ���ȡ�����б�
	 * @param tableName
	 * @return
	 * @throws CodegenException
	 */
	private List<ColumnModel> getColumnsByTable(String tableName) throws CodegenException
	{
		tableName=tableName.toUpperCase();
		DaoHelper<ColumnModel> dao=new DaoHelper<ColumnModel>(this.url, this.username, this.password);
		String sql=String.format(sqlColumn,tableName);
		List<ColumnModel> list=dao.queryForList(sql, new H2MapCmd());
		return list;
	}



	/**
	 * ȡ�ñ�ע��
	 * @param tableName
	 * @return
	 * @throws CodegenException
	 */
	private String getTableComment(String tableName) throws CodegenException {
		tableName=tableName.toUpperCase();
		
		String sql = String.format(sqlTableComment, tableName);
		DaoHelper<String> dao=new DaoHelper<String>(url, username, password);
		String comment= dao.queryForObject(sql,new MapCmd<String>() {
			public String getObjecFromRs(ResultSet rs) throws SQLException {
				return rs.getString("REMARKS");
			}
		});
		return comment==null?tableName:comment;

	}
	
	/**
	 * ȡ�ñ������
	 * @param tableName
	 * @return
	 * @throws CodegenException
	 */
//	private String getPk(String tableName) throws CodegenException
//	{
//		tableName=tableName.toUpperCase();
//		String sql = String.format(sqlPk, tableName);
//		DaoHelper<String> dao=new DaoHelper<String>(url, username, password);
//		String pk="";
//		try {
//			pk= dao.queryForObject(sql,new MapCmd<String>() {
//				public String getObjecFromRs(ResultSet rs) throws SQLException{
//					return rs.getString("COLNAME");
//					
//				}
//			});
//		} catch (Exception e) {
//			throw new CodegenException("�ӱ���ȡ����������,������Ƿ���������");
//		}
//		return pk;
//		
//	}

//	/**
//	 * ��������
//	 * @param list
//	 * @param pk
//	 */
//	private void setPk(List<ColumnModel> list ,String pk)
//	{
//		for(ColumnModel model :list){
//			if(pk.toLowerCase().equals(model.getColumnName().toLowerCase()))
//				model.setIsPK(true);
//		}
//	}
	
	/**
	 * ���ݱ���ȡ�ñ����
	 * @param tableName
	 */
	public TableModel getByTable(String tableName) throws CodegenException {
		tableName=tableName.toUpperCase();
		//ȡ��ע��
		String tabComment=getTableComment(tableName);
//		String pk=getPk(tableName);
		
		TableModel tableModel = new TableModel();
		tableModel.setTableName(tableName);
		tableModel.setTabComment(tabComment);
		//ȡ�ñ���б�����
		List<ColumnModel> list=getColumnsByTable(tableName);
		tableModel.setColumnList(list);
 
		return tableModel;
	}

	/**
	 * ȡ�����ݿ�����б�
	 */
	public List<String> getAllTable() throws CodegenException {

		DaoHelper<String> dao=new DaoHelper<String>(url, username, password);
		return dao.queryForList(sqlAllTables,new  MapCmd<String>() {
			public String getObjecFromRs(ResultSet rs) throws SQLException {
				return rs.getString("TABLE_NAME");
			}
		});
	}
}
