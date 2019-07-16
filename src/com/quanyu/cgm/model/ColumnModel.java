package com.quanyu.cgm.model;

/**
 * ���ݿ��ж���<br>
 * ������������ע�ͣ������ͣ����ݿ����ͣ��������ֶγ��ȡ�
 * @author quanyu
 *
 */
public class ColumnModel {

	// ����
	private String columnName = "";
	// ��ע��
	private String comment = "";
	// ������
	private String colType = "";
	// �����ݿ�����
	private String colDbType = "";
	// �Ƿ�����
	private boolean isPK = false;
	// �ֶγ���
	private long length = 0;
	// ����
	private int precision = 0;
	// С��λ
	private int scale = 0;
	// �������ֶ�
	private int autoGen = 0;
	//��Ϊ��
	private boolean isNotNull = false;
	
	private String displayDbType="";
	
	/**
	 * �����ơ�<br>
	 * ��ģ���е�ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;${columnModel.columnName}<br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}
	
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	
	
	/**
	 * ��ע�͡�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;		${columnModel.comment}<br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * �����͡�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;		${columnModel.colType}<br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
	

	/**
	 * ���ݿ�ʵ�ʵ������͡�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;		${columnModel.colDbType}<br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public String getColDbType() {
		return colDbType;
	}
	public void setColDbType(String colDbType) {
		this.colDbType = colDbType;
	}
	
	/**
	 * �Ƿ���������<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;		<#if !columnModel.isPK><br>
	 *
	 * &nbsp;&nbsp;&nbsp;&nbsp;		&lt;/#if><br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public boolean getIsPK() {
		return isPK;
	}
	public void setIsPK(boolean isPK) {
		this.isPK = isPK;
	}
	
	/**
	 * ���ݿ��еĳ��ȣ��ַ��������ͳ��ȡ�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;		${columnModel.length}<br>
	 * &lt;/#list><br>
	 * 
	 * @return
	 */
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	/**
	 * �����о��ȡ�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *	&nbsp;&nbsp;&nbsp;&nbsp;	${columnModel.precision}<br>
	 * &lt;/#list><br>
	 * 
	 * @return
	 */
	public int getPrecision() {
		return precision;
	}
	
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	/**
	 * ������С����λ����<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *	&nbsp;&nbsp;&nbsp;&nbsp;	${columnModel.scale}<br>
	 * &lt;/#list><br>
	 * 
	 * @return
	 */
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	/**
	 * �ֶ�������������SQL2005) 1,Ϊ��������<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * 
	 * <#list model.columnList as columnModel><br>
	 *	&nbsp;&nbsp;&nbsp;&nbsp;	${columnModel.autoGen}<br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public int getAutoGen() {
		return autoGen;
	}
	public void setAutoGen(int autoGen) {
		this.autoGen = autoGen;
	}
	
	/**
	 * �ֶ��Ƿ�ǿա�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *	&nbsp;&nbsp;&nbsp;&nbsp;	<#if !columnModel.isNotNull><br>
	 *		&lt;/#if><br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public boolean getIsNotNull() {
		return isNotNull;
	}
	public void setIsNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}
	
	/**
	 * ��ʾ���ֶ����͡�<br>
	 * ��ģ����ʹ�÷�����<br>
	 * 
	 * <#list model.columnList as columnModel><br>
	 *	&nbsp;&nbsp;&nbsp;&nbsp;	<#if !columnModel.displayDbType><br>
	 *		&lt;/#if><br>
	 * &lt;/#list>
	 * 
	 * @return
	 */
	public String getDisplayDbType() {
		return displayDbType;
	}
	public void setDisplayDbType(String displayDbType) {
		this.displayDbType = displayDbType;
	}
	
	/**
	 * ���ԡ�<br>
	 * <pre>
	 * 	&lt;var>
	 *		&lt;var-name>regex&lt;/var-name>
	 *		&lt;var-value>������ʽ&lt;/var-value>
	 *	&lt;/var>
	 * </pre>
	 * @return
	 */
	public String getDisplay()
	{
		return "";
	}


	@Override
	public String toString() {
		return "ColumnModel [columnName=" + columnName + ", comment=" + comment + ", colType=" + colType + ", colDbType=" + colDbType + ", isPK=" + isPK
				+ ", length=" + length + ", precision=" + precision + ", scale=" + scale + ", autoGen=" + autoGen + ", isNotNull=" + isNotNull
				+ ", displayDbType=" + displayDbType + "]";
	}
	
	
}
