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
 * ȡ�����ݿ��ӿ�IDbHelper��DB2 ��ʵ��
 *
 */
public class DB2Helper implements IDbHelper {

	// ȡ������
	private String sqlPk = 
		"SELECT "+ 
			"TABNAME TAB_NAME, "+
			"COLNAME COL_NAME, "+
			"KEYSEQ  "+
		"FROM  "+
			"SYSCAT.COLUMNS "+ 
		"WHERE  "+
			"TABSCHEMA='BPMX380' AND KEYSEQ>0 AND UPPER(TABNAME) = UPPER('%s')";

	// ȡ��ע��
	private String sqlTableComment = 
		"SELECT  "+
			"TABNAME, "+
			"REMARKS "+
		"FROM  "+
			"SYSCAT.TABLES "+
		"WHERE "+
			"TABSCHEMA IN (SELECT CURRENT SCHEMA FROM SYSIBM.DUAL) "+
			"AND UPPER(TABNAME) = UPPER('%s') ";

	// ȡ���б�
	private String sqlColumn = 
			"SELECT "+ 
					"TABNAME, "+
					"COLNAME, "+
					"TYPENAME, "+
					"REMARKS, "+
					"NULLS, "+
					"LENGTH, "+
					"SCALE, "+
					"KEYSEQ  "+
				"FROM  "+
					"SYSCAT.COLUMNS "+ 
				"WHERE  "+
					"TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) "+
					"AND UPPER(TABNAME) = UPPER('%s') ";
			
	// ȡ�����ݿ����б�
	private String sqlAllTables =
				"SELECT  "+
					"TABNAME, "+
					"REMARKS "+
				"FROM  "+
					"SYSCAT.TABLES "+
				"WHERE  "+
					"TABSCHEMA IN (SELECT CURRENT SQLID FROM SYSIBM.DUAL) ";

	private String url;
	private String username;
	private String password;

	public DB2Helper() throws CodegenException {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (ClassNotFoundException e) {
			throw new CodegenException("�Ҳ���DB2����!", e);
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
		List<ColumnModel> list=dao.queryForList(sql, new DB2MapCmd());
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
				return rs.getString("TABNAME");
			}
		});
	}
	
	public static void main(String[] args) throws CodegenException
	{
		
		DB2Helper helper=new DB2Helper();
		helper.setUrl("jdbc:db2://192.168.1.17:50000/bpmx:currentSchema=BPMX380;", "db2admin", "123456");
//		String pk= helper.getPk("TEST");
//		System.out.println(helper.getAllTable().size());
		System.out.println(helper.getByTable("TEST1"));

	}
	
	

}
