package com.quanyu.cgm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ���ݱ����
 * 
 * @author quanyu
 * 
 */
public class TableModel {

	// ����
	private String tableName;
	// ��ע��
	private String tabComment;
	//�������
	private String foreignKey = "";
	
	private Map<String, String> variables=new HashMap<String, String>();

	// �����е��ж����б�
	private List<ColumnModel> columnList = new ArrayList<ColumnModel>();

	// �ӱ�����
	private List<TableModel> subTableList = new ArrayList<TableModel>();
	//�Ƿ����ӱ�
	private boolean sub;

	/**
	 * ����
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * ��ע��
	 * 
	 * @return
	 */
	public String getTabComment() {
		return tabComment.replaceAll("\r\n", "");
	}

	public void setTabComment(String tabComment) {
		this.tabComment = tabComment;
	}
	

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}
	
	public boolean getSub(){
		return sub;
	}

	/**
	 * ������ݿ���
	 * 
	 * @return
	 */
	public List<ColumnModel> getColumnList() {
		return columnList;
	}
	
	/**
	 * ȡ�������б�
	 * @return
	 */
	public List<ColumnModel> getPkList()
	{
		List<ColumnModel> list=new ArrayList<ColumnModel>();
		for(ColumnModel col :columnList){
			if(col.getIsPK())
				list.add(col);
		}
		return list;
	}
	
	/**
	 * ȡ����������
	 * @return
	 */
	public ColumnModel getPkModel()
	{
		for(ColumnModel col :columnList){
			if(col.getIsPK()){
				return col;
			}
		}
		return null;
	}
	
	/**
	 * ȡ����ͨ�е��б�
	 * @return
	 */
	public List<ColumnModel> getCommonList()
	{
		List<ColumnModel> list=new ArrayList<ColumnModel>();
		for(ColumnModel col :columnList){
			if(!col.getIsPK())
				list.add(col);
		}
		return list;
	}
	

	public void setColumnList(List<ColumnModel> columnList) {
		this.columnList = columnList;
	}

	/**
	 * �ӱ�����
	 * 
	 * @return
	 */
	public List<TableModel> getSubTableList() {
		return subTableList;
	}

	public void setSubTableList(List<TableModel> subTableList) {
		this.subTableList = subTableList;
	}
	
	/**
	 * �������������
	 * @return
	 */
	public String getForeignKey() {
		return foreignKey;
	}
	
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	
	public Map<String, String> getVariables() {
		return variables;
	}


	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		return "TableModel [tableName=" + tableName + ", tabComment=" + tabComment + ", foreignKey=" + foreignKey + ", variables=" + variables
				+ ", columnList=" + columnList + ", subTableList=" + subTableList + ", sub=" + sub + "]";
	}
	
	
	
	

}
