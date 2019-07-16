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
 * ȡ�����ݿ��ӿ�IDbHelper��ORACLE ��ʵ��
 *
 */
public class OracleHelper implements IDbHelper {

	// ȡ������
	private String sqlPk = "select column_name from user_constraints c,user_cons_columns col where c.constraint_name=col.constraint_name and c.constraint_type='P' and c.table_name='%s'";

	// ȡ��ע��
	private String sqlTableComment = "select * from user_tab_comments  where table_type='TABLE' AND table_name ='%s'";

	// ȡ���б�
	private String sqlColumn = "select    A.column_name NAME,A.data_type TYPENAME,A.data_length LENGTH,A.data_precision PRECISION,    A.Data_Scale SCALE,A.Data_default, A.NULLABLE, B.comments DESCRIPTION "
			+ " from  user_tab_columns A,user_col_comments B where a.COLUMN_NAME=b.column_name and    A.Table_Name = B.Table_Name and A.Table_Name='%s' order by A.column_id";

	// ȡ�����ݿ����б�
	private String sqlAllTables = "select table_name from user_tables where status='VALID'";

	private String url;
	private String username;
	private String password;

	public OracleHelper() throws CodegenException {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new CodegenException("�Ҳ���oracle����!", e);
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
		List<ColumnModel> list=dao.queryForList(sql, new OracleMapCmd());
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
				return rs.getString("COMMENTS");
			}
		});
		if(comment==null) comment=tableName;
		String[] aryComment=comment.split("\n");
		return aryComment[0];

	}
	
	/**
	 * ȡ�ñ������
	 * @param tableName
	 * @return
	 * @throws CodegenException
	 */
	private String getPk(String tableName) throws CodegenException
	{
		tableName=tableName.toUpperCase();
		String sql = String.format(sqlPk, tableName);
		DaoHelper<String> dao=new DaoHelper<String>(url, username, password);
		String pk="";
		try {
			pk= dao.queryForObject(sql,new MapCmd<String>() {
				public String getObjecFromRs(ResultSet rs) throws SQLException{
					return rs.getString("COLUMN_NAME");
					
				}
			});
		} catch (Exception e) {
			throw new CodegenException("�ӱ���ȡ����������,������Ƿ���������");
		}
		return pk;
		
	}

	/**
	 * ��������
	 * @param list
	 * @param pk
	 */
	private void setPk(List<ColumnModel> list ,String pk)
	{
		for(ColumnModel model :list){
			if(pk.toLowerCase().equals(model.getColumnName().toLowerCase()))
				model.setIsPK(true);
		}
	}
	
	/**
	 * ���ݱ���ȡ�ñ����
	 * @param tableName
	 */
	public TableModel getByTable(String tableName) throws CodegenException {
		tableName=tableName.toUpperCase();
		//ȡ��ע��
		String tabComment=getTableComment(tableName);
		String pk=getPk(tableName);
		
		TableModel tableModel = new TableModel();
		tableModel.setTableName(tableName);
		tableModel.setTabComment(tabComment);
		//ȡ�ñ���б�����
		List<ColumnModel> list=getColumnsByTable(tableName);
		//��������
		if(StringUtil.isNotEmpty(pk)){
			setPk(list,pk);
		}
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
	
	public static void main(String[] args) throws CodegenException
	{
		
		OracleHelper helper=new OracleHelper();
		helper.setUrl("jdbc:oracle:thin:@localhost:1521:zyp", "zyp", "zyp");
		String pk= helper.getPk("TEST");
		

	}


}
