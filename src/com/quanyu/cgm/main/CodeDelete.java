package com.quanyu.cgm.main;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

import com.quanyu.cgm.exception.CodegenException;
import com.quanyu.cgm.model.ConfigModel;
import com.quanyu.cgm.model.ConfigModel.Files;
import com.quanyu.cgm.model.ConfigModel.Table;
import com.quanyu.cgm.model.ConfigModel.Table.SubTable;
import com.quanyu.cgm.util.FileHelper;
import com.quanyu.cgm.util.StringUtil;

import freemarker.template.utility.Execute;

public class CodeDelete {
	private static String xmlPath;

	public static void setXmlPath(String path) {
		xmlPath = path;
	}
	
	/**
	 * ���ݸ�����schemaУ��xml,�������Ƿ�У��ɹ�.
	 * @param xml
	 * @param schema
	 * @return
	 */
	public String validXmlBySchema(String xsdPath) {
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

       // ��ʼ��֤���ɹ����success!!!��ʧ�����fail
       try {
           validator.validate(source);
       }catch (Exception ex) {
           return ex.getMessage();
       }
       return null;

	}
	
	public ConfigModel loadXml(String xmlPath)throws Exception{
		// xsd�ļ�·��
		String xsdPath=new File("").getAbsolutePath()+"\\codegen.xsd";
		// ��֤XML
		String result= validXmlBySchema(xsdPath);
		
		if (result!=null) {
			throw new CodegenException("XML�ļ�ͨ��XSD�ļ�У��ʧ��:"+result);
		}
		// ��֤ͨ���� ��ʼ��ȡXML
		ConfigModel cm = new ConfigModel();

		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new File(xmlPath));
		} catch (DocumentException e) {
			throw new CodegenException("��ȡXML����!", e);
		}
		Element root = document.getRootElement();
		// ��XML�ж�ȡtable
		Properties prop=new Properties();
		String pathBase=new File("").getAbsolutePath();
		InputStream in=new BufferedInputStream(new FileInputStream(pathBase+"\\"+"codegen.properties"));
		prop.load(in);
		String charset=prop.getProperty("charset");
		String system=prop.getProperty("system");
		cm.setCharset(charset);
		for (Iterator i = root.elementIterator("table"); i.hasNext();) {
			Element tableEl = (Element) i.next();
			String tableName = tableEl.attributeValue("tableName");
		
			Table table = cm.new Table(tableName);
			for (Iterator j = tableEl.elementIterator("variable"); j.hasNext();) {
				Element fileEl = (Element) j.next();
				String name = fileEl.attributeValue("name");
				String value = fileEl.attributeValue("value");
				table.getVariable().put(name, value);
				if(StringUtil.isNotEmpty(table.getVariable().get("class"))){
					String classVar=StringUtil.toFirstLowerCase(table.getVariable().get("class"));
					table.getVariable().put("classVar", classVar);
				}
			}
			table.getVariable().put("tabname", tableName);
			//����ӱ�
			for (Iterator m = tableEl.elementIterator("subtable"); m.hasNext();) {
				Element subEl = (Element) m.next();
				String tablename = subEl.attributeValue("tablename");
				String foreignKey = subEl.attributeValue("foreignKey");
				Map<String,String >vars=new HashMap<String, String>();
				
				for (Iterator var = subEl.elementIterator("variable"); var.hasNext();) {			
					Element varEl = (Element) var.next();
					String name = varEl.attributeValue("name");
					String value = varEl.attributeValue("value");
					vars.put(name, value);
				}
				if(StringUtil.isNotEmpty(vars.get("class"))){
					String classVar=StringUtil.toFirstLowerCase(vars.get("class"));
					vars.put("classVar", classVar);
				}
				vars.put("tabname", tablename);
				
				//����ӱ������
				table.addSubTable(tablename, foreignKey, vars);
			}
			cm.getTables().add(table);
		
		}
		//��xml��ȡ�ļ�ģ��
		Element filesEl= root.element("files");
		Files files=cm.new Files();
		cm.setFiles(files);
		
		String baseDir = filesEl.attributeValue("baseDir");
		files.setBaseDir(baseDir);
		for (Iterator j = filesEl.elementIterator("file"); j.hasNext();) {
			
			Element fileEl = (Element) j.next();
			String refTemplate = fileEl.attributeValue("refTemplate");
			String filename = fileEl.attributeValue("filename");
			String dir = StringUtil.replaceVariable(fileEl.attributeValue("dir"),system);
			
			String sub=fileEl.attributeValue("sub");
			String override=fileEl.attributeValue("override");
			boolean isOverride=false;
			if(StringUtil.isNotEmpty(override)&& override.equals("true")){
				isOverride=true;
			}
			String append=fileEl.attributeValue("append");
			if(append!=null && append.toLowerCase().equals("true")){
				String insertTag=fileEl.attributeValue("insertTag");
				String startTag=fileEl.attributeValue("startTag");
				String endTag=fileEl.attributeValue("endTag");
				if(insertTag==null)
					insertTag="<!--insertbefore-->";
				if(StringUtil.isEmpty(startTag)) startTag="";
				if(StringUtil.isEmpty(endTag)) endTag="";
				if(sub!=null&&sub.toLowerCase().equals("true")){
					files.addFile(null, filename, dir,true,isOverride,true,insertTag,startTag,endTag);
				}else{
					files.addFile(null, filename, dir,false,isOverride,true,insertTag,startTag,endTag);
				}
			}else{
				if(sub!=null&&sub.toLowerCase().equals("true")){
					files.addFile(null, filename, dir,true,isOverride,false,"","","");
				}else{
					files.addFile(null, filename, dir,false,isOverride,false,"","","");
				}
			}
		}
		return cm;
	}
	
	/**
	 * �������� ɾ����Ӧ�����ɵĴ���
	 * @param configModel
	 * @throws Exception
	 */
	public  void deleteFileByConfig(ConfigModel configModel)throws Exception{
		List<Table> tableList=configModel.getTables();
		Files files=configModel.getFiles();
		String baseDir=files.getBaseDir();
		String charset=configModel.getCharset();
		for(Table table:tableList){
			Map<String,String> variables=table.getVariable();
			List<ConfigModel.Files.File> list=files.getFiles();
			for(ConfigModel.Files.File file: list){
				String filename=file.getFilename();
				String dir=file.getDir();
				filename=StringUtil.replaceVariable(filename, variables);
				dir=StringUtil.replaceVariable(dir, variables);
				
				String fileDir=baseDir+"\\"+dir;
				String startTag=StringUtil.replaceVariable(file.getStartTag(), variables);
				String endTag=StringUtil.replaceVariable(file.getEndTag(), variables);
				boolean isAppend=file.getAppend();
				if(isAppend){
					deleteAppendFile(fileDir,filename,charset,startTag,endTag);
				}else{
					deleteFile(fileDir,filename);
				}
			}
			List<SubTable> subtables=table.getSubtable();
			if(subtables!=null&& subtables.size()!=0){
				for(SubTable subtable:subtables){
					Map<String,String> vars=subtable.getVars();
					for(ConfigModel.Files.File file: list){
						String filename=file.getFilename();
						String dir=file.getDir();
						if(filename.indexOf("{")!=-1){
							String var=filename.substring(filename.indexOf('{')+1,filename.indexOf('}'));
							filename=vars.get(var)+filename.substring(filename.indexOf('}')+1);
						}
						if(dir.indexOf("{")!=-1){
							String var=dir.substring(dir.indexOf('{')+1,dir.indexOf('}'));
							dir=dir.substring(0,dir.indexOf('{'))+vars.get(var);
						}
						String fileDir=baseDir+"\\"+dir;
						boolean isSub=file.isSub();
						boolean isAppend=file.getAppend();
						String startTag=StringUtil.replaceVariable(file.getStartTag(), vars);
						String endTag=StringUtil.replaceVariable(file.getStartTag(), vars);
						if(isSub){
							if(isAppend){
								deleteAppendFile(fileDir,filename,charset,file.getStartTag(),file.getEndTag());
							}else{
								deleteFile(fileDir,filename);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * ����Ŀ¼���ļ��� ɾ�����ɵĴ����ļ�
	 * �����Ŀ¼Ϊ��  ��ɾ����Ŀ¼
	 * @param fileDir
	 * @param filename
	 */
	private void deleteFile(String fileDir,String filename) {
		String filepath=StringUtil.combilePath(fileDir, filename);
		File file=new File(filepath);
		if(file.exists()){
			file.delete();
			System.out.println("ɾ�����ļ�"+filename);
		}else{
			System.out.println(filename+"���ļ�������");
		}
		if(!FileHelper.isExistFile(fileDir)){
			file=new File(fileDir);
			file.delete();
		}
	}
	
	/**
	 * ɾ�����ɵĴ���
	 * @param fileDir
	 * @param filename
	 * @param charset
	 * @param startTag
	 * @param endTag
	 * @throws Exception
	 */
	private void deleteAppendFile(String fileDir,String filename,String charset,String startTag,String endTag) throws Exception{
		String filepath=StringUtil.combilePath(fileDir, filename);
		File file=new File(filepath);
		if(file.exists()){
			boolean exists=false;
			String content=FileHelper.readFile(filepath,charset);
			if(content.indexOf(startTag)!=-1){
				content=content.substring(0,content.indexOf(startTag)).trim()+"\r\n"
							+content.substring(content.indexOf(endTag)+endTag.length()).trim();
				file.delete();
				file=new File(filepath);
				FileHelper.writeFile(file, charset, content.trim());
				System.out.println("ɾ��������"+startTag+"-----"+endTag);
			}
		}
	}

	public void execute() throws Exception {
		String dir=new File("").getAbsolutePath();
        if(xmlPath==null||xmlPath==""){
        	setXmlPath(dir+"\\"+"codegenconfig.xml");
        }
		System.out.println("execute:" +xmlPath);
		if (xmlPath == null) {
			throw new CodegenException("δָ��XML·��!");
		}
		ConfigModel cm = loadXml(xmlPath);
		deleteFileByConfig(cm);
	}
	
	public static void main(String[] args) throws Exception{
		CodeDelete cd = new CodeDelete();
		cd.setXmlPath("D:\\workspace\\codegen\\src\\mscodegenconfig.xml");
		cd.execute();
	}

}
