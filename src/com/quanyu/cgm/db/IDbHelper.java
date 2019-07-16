package com.quanyu.cgm.db;

import java.util.List;

import com.quanyu.cgm.exception.CodegenException;
import com.quanyu.cgm.model.TableModel;

/**
 * ��ȡ���ݿ����б�ͱ��TableMode�ӿ��ࡣ
 * @author quanyu
 *
 */
public interface IDbHelper {

	/**
	 * ����URL,username,password
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	void setUrl(String url, String username, String password);

	/**
	 * ���ݱ���ȡ��TableModel
	 * 
	 * @param tableName
	 * @return
	 */
	TableModel getByTable(String tableName) throws CodegenException;

	/**
	 * ȡ�����еı���
	 * 
	 * @return
	 */
	List<String> getAllTable() throws CodegenException;
}
