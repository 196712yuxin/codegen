package com.quanyu.cgm.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  ResultSet ����ӳ��ӿڣ��û�����ͨ��������������ʵ�֡�
 *  
 * @author quanyu
 *
 * @param <T>
 */
public interface MapCmd<T> {
	
	/**
	 * ����ResultSet ��¼�����󷵻ض���T ��
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public T getObjecFromRs(ResultSet rs) throws SQLException;
	

}
