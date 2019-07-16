package com.quanyu.cgm.util;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XmlUtil {
	/**
	 * ���ݸ�����schemaУ��xml,�������Ƿ�У��ɹ�.
	 * <pre>
	 * �ɹ����ؿմ���
	 * ʧ�ܷ��� ������Ϣ��
	 * </pre>
	 * @param xsdPath	schema�ļ�·��
	 * @param xmlPath	����֤��xsd�ļ�·����
	 * @return
	 */
	public static String validXmlBySchema(String xsdPath,String xmlPath) {
		 // ����schema����
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance("http://www.w3.org/2001/XMLSchema");

        // ������֤�ĵ��ļ��������ô��ļ���������װ���ļ�����schema��֤
        File schemaFile = new File(xsdPath);
        
        
        // ����schema������������֤�ĵ��ļ���������Schema����
        Schema schema = null;
        try {
            schema = schemaFactory.newSchema(schemaFile);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        // ͨ��Schema��������ڴ�Schema����֤��������schenaFile������֤
        Validator validator = schema.newValidator();

        // �õ���֤������Դ
        Source source = new StreamSource(FileHelper.getInputStream(xmlPath));

     
        try {
            validator.validate(source);
        }catch (Exception ex) {
        	return ex.getMessage();
        }
        return "";
	}
}
