package com.quanyu.cgm.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.quanyu.cgm.db.IDbHelper;
import com.quanyu.cgm.exception.CodegenException;
import com.quanyu.cgm.model.ConfigModel;
import com.quanyu.cgm.model.ConfigModel.Files;
import com.quanyu.cgm.model.ConfigModel.GenAll;
import com.quanyu.cgm.model.ConfigModel.Table;
import com.quanyu.cgm.model.ConfigModel.Templates;
import com.quanyu.cgm.model.TableModel;
import com.quanyu.cgm.util.FileHelper;
import com.quanyu.cgm.util.StringUtil;
import com.quanyu.cgm.util.XmlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Codegen {

	private static String rootPath;

	public static void setRootPath(String path) {
		rootPath = path;
	}
	
	private static String getRootPath(){
		if(rootPath.isEmpty()){
			rootPath=new File("").getAbsolutePath();
		}
		if(!rootPath.endsWith("\\"))
			rootPath +="\\";
		return rootPath;
	}
	
	private static String getXmlPath() throws CodegenException{
		String configXml=getRootPath() + "codegenconfig.xml";
		File file=new File(configXml);
		if(!file.exists()){
			throw new CodegenException( "��ȷ�������ļ���"+configXml +"�Ƿ����!");
		}
		return configXml;
	}
	
	
	private static String getXsdPath() throws CodegenException{
		String path= getRootPath() +"codegen.xsd";
		File file=new File(path);
		if(!file.exists()){
			throw new CodegenException("schema�ļ�" + path +"������");
		}
		return path;
	}
	
	
	private static String getPropertiesPath() throws CodegenException{
		String path= getRootPath() +"codegen.properties";
		File file=new File(path);
		if(!file.exists()){
			throw new CodegenException("�������������ļ�" + path +"������");
		}
		return path;
	}

	
	
	/**
	 * ��ȡXML����
	 * 
	 * @param xmlFile
	 * @return
	 * @throws CodegenException
	 */
	@SuppressWarnings("rawtypes")
	public ConfigModel loadXml() throws Exception {
		// xsd�ļ�·��
		String xsdPath=getXsdPath();
		
		String xmlPath=getXmlPath();
		
		// ��֤XML
		String result = XmlUtil.validXmlBySchema(xsdPath,xmlPath);
		
		if (!result.equals("")) {
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
	
		// ��XML�ж�ȡvariables
		Element variablesEl= root.element("variables");
		for (Iterator j = variablesEl.elementIterator("variable"); j.hasNext();) {
			Element variableEl = (Element) j.next();
			String name = variableEl.attributeValue("name");
			String value = variableEl.attributeValue("value");
			cm.getVariables().put(name, value);
		}

		// ��XML�ж�ȡtemplates
		Element templatesEl= root.element("templates");
		//String basepath = templatesEl.attributeValue("basepath");
		Properties prop=new Properties();
		
		String propath=getPropertiesPath();
		
		InputStream in=new BufferedInputStream(new FileInputStream(propath));
		prop.load(in);
		String charset=prop.getProperty("charset");
		String dbHelperClass=prop.getProperty("dbHelperClass");
		String url=prop.getProperty("url");
		String username=prop.getProperty("username");
		String password=prop.getProperty("password");
		String system=prop.getProperty("system");
		cm.getVariables().put("system", system);
		cm.setCharset(charset);
		cm.setDatabase(cm.new Database(dbHelperClass, url, username, password));
		
		String basepath=getRootPath()+"template";
		Templates templates = cm.new Templates(basepath);
		cm.setTemplates(templates);
		for (Iterator j = templatesEl.elementIterator("template"); j.hasNext();) {
			Element templateEl = (Element) j.next();
			String id = templateEl.attributeValue("id");
			String path = templateEl.attributeValue("path");
			templates.getTemplate().put(id, path);
		}
		
		//��xml��ȡ�ļ�ģ��
		Element filesEl= root.element("files");
		if(filesEl!=null){
			Files files=cm.new Files();
			cm.setFiles(files);
			
			String baseDir = filesEl.attributeValue("baseDir");
			files.setBaseDir(baseDir);
			for (Iterator j = filesEl.elementIterator("file"); j.hasNext();) {
				
				Element fileEl = (Element) j.next();
				String refTemplate = fileEl.attributeValue("refTemplate");
				String filename = fileEl.attributeValue("filename");
				String dir = StringUtil.replaceVariable(fileEl.attributeValue("dir"), system);
				
				String template= templates.getTemplate().get(refTemplate);
				if(template==null)
					throw new CodegenException("û���ҵ�" +refTemplate +"��Ӧ��ģ��!");
			
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
						files.addFile(template, filename, dir,true,isOverride,true,insertTag,startTag,endTag);
					}else{
						files.addFile(template, filename, dir,false,isOverride,true,insertTag,startTag,endTag);
					}
					
				}else{
					if(sub!=null&&sub.toLowerCase().equals("true")){
						files.addFile(template, filename, dir,true,isOverride,false,"","","");
					}else{
						files.addFile(template, filename, dir,false,isOverride,false,"","","");
					}
				}
			}
			// ��XML�ж�ȡtable
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
						if(StringUtil.isNotEmpty(vars.get("class"))){
							String classVar=StringUtil.toFirstLowerCase(vars.get("class"));
							vars.put("classVar", classVar);
						}
					}
					
					//����ӱ������
					table.addSubTable(tablename, foreignKey, vars);
				}
				cm.getTables().add(table);
			}
		}
		

		// ��XML�ж�ȡgenAll
		for (Iterator i = root.elementIterator("genAll"); i.hasNext();) {
			Element tableEl = (Element) i.next();
			String tableNames = tableEl.attributeValue("tableNames");
			GenAll genAll = cm.new GenAll(tableNames);
			cm.setGenAll(genAll);
			for (Iterator j = tableEl.elementIterator("file"); j.hasNext();) {
				Element fileEl = (Element) j.next();
				String refTemplate = fileEl.attributeValue("refTemplate");
				String filename = fileEl.attributeValue("filename");
				String extName = fileEl.attributeValue("extName");
				String dir = fileEl.attributeValue("dir");
				String genMode = fileEl.attributeValue("genMode");
				String template = cm.getTemplates().getTemplate().get(refTemplate);
				if (template == null) {
					throw new CodegenException("�Ҳ���ģ�壺 " + refTemplate + "!");
				}
				if ("SingleFile".equals(genMode) && filename == null) {
					throw new CodegenException("��SingleFileģʽʱ������Ҫ��filename!");
				} else if ("MultiFile".equals(genMode) && extName == null) {
					throw new CodegenException("��MultiFileģʽʱ������Ҫ��extName!");
				}
				GenAll.File file = genAll.new File(template, filename, extName, dir, genMode);
				genAll.getFile().add(file);
				for (Iterator k = fileEl.elementIterator("variable"); k.hasNext();) {
					Element variableEl = (Element) k.next();
					String name = variableEl.attributeValue("name");
					String value = variableEl.attributeValue("value");
					file.getVariable().put(name, value);
				}
			}
		}
		return cm;
	}

	/**
	 * �������û�ȡ��Ԫ�����б�
	 * @param dbHelper
	 * @param configModel
	 * @return
	 * @throws CodegenException
	 */
	private List<TableModel> getTableModelList(IDbHelper dbHelper,ConfigModel configModel) throws CodegenException{
		List<ConfigModel.Table> tables = configModel.getTables();
		List<TableModel> tableModels=new ArrayList<TableModel>();
		for (ConfigModel.Table table : tables) {
			String tbName=table.getTableName();
			// �����ݿ��ж�ȡ��tableModel
			TableModel tableModel = dbHelper.getByTable(tbName);
			//���ñ�
			tableModel.setVariables(table.getVariable());
			tableModel.setSub(false);
			//��Ӵӱ�
			List<ConfigModel.Table.SubTable> subtables= table.getSubtable();
			for(ConfigModel.Table.SubTable sb :subtables){
				String tableName=sb.getTableName();
				String foreignKey=sb.getForeignKey();
				Map<String,String> variables=sb.getVars();
				TableModel subTable=dbHelper.getByTable(tableName);
				subTable.setVariables(variables);
				subTable.setSub(true);
				subTable.setForeignKey(foreignKey);
				tableModel.getSubTableList().add(subTable);
				tableModels.add(subTable);
			}
			tableModels.add(tableModel);
		}
		return tableModels;
	}
//				
	/**
	 * �����������ɡ�
	 * @param configModel
	 * @throws CodegenException
	 */
	private void genTableByConfig(IDbHelper dbHelper,ConfigModel configModel,Configuration cfg) throws CodegenException {
		try {
			
			List<TableModel> tableModels= getTableModelList(dbHelper,configModel);
			
			if(tableModels==null || tableModels.size()==0){
				System.out.println("û��ָ�����ɵı�!");
				return;
			}
				
			
			Files files= configModel.getFiles();
			String baseDir= files.getBaseDir();
			
			List<ConfigModel.Files.File> fileList= files.getFiles();
			if(fileList==null || fileList.size()==0){
				System.out.println("û��ָ�����ɵ��ļ�!");
				return;
			}
				
			
			System.out.println("*********��ʼ����**********");
			
			for(TableModel tableModel:tableModels){
				String tbName=tableModel.getTableName();
				Map<String,String> variables=tableModel.getVariables();
				boolean isSub=tableModel.getSub();
				//���������ļ�
				for(ConfigModel.Files.File file :fileList){
					//�ļ���
					String filename=file.getFilename();
					String startTag=file.getStartTag();
					String endTag=file.getEndTag();
					boolean sub=file.isSub();
					boolean override=file.isOverride();
					if(isSub==true&&sub==false)continue;
					startTag=StringUtil.replaceVariable(startTag, tbName);
					endTag=StringUtil.replaceVariable(endTag, tbName);
					//�����ļ�Ŀ¼
					String dir=file.getDir();
					filename=StringUtil.replaceVariable(filename, variables);
					dir=StringUtil.replaceVariable(dir, variables);
					dir=StringUtil.combilePath(baseDir, dir);
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("model", tableModel);
					map.put("vars", configModel.getVariables());
					map.put("date", new Date());
					
					//�ļ���ӡ�
					if(file.getAppend()){
						appendFile(dir,filename, file.getTemplate(), configModel.getCharset(), cfg, map,file.getInsertTag(),startTag, endTag);
					}
					else{
						if(override){
							genFile(dir,filename, file.getTemplate(), configModel.getCharset(), cfg, map);
						}else{
							File f = new File(dir+"\\"+filename);
							if(!f.exists()){
								genFile(dir,filename, file.getTemplate(), configModel.getCharset(), cfg, map);
							}
						}
					}
					System.out.println(filename + " ���ɳɹ�!");
				}
			}
			System.out.println("*********�����ļ����ɳɹ�!**********");
		} catch (IOException e) {
			throw new CodegenException(e);
		} catch (TemplateException e) {
			throw new CodegenException("freemarkerִ�г���!", e);
		}

	}
	
	private void getAllTable(IDbHelper dbHelper,ConfigModel configModel,Configuration cfg ) throws CodegenException, IOException, TemplateException{
		// XML�е�genAll����
		GenAll genAll = configModel.getGenAll();
		if(genAll==null) return;
		
		List<String> tableNames = null;
		if (genAll.getTableNames() == null) {
			tableNames = dbHelper.getAllTable();
		} else {
			tableNames = new ArrayList<String>();
			for (String name : genAll.getTableNames().replaceAll(" ", "").split(",")) {
				if (name.length() > 0) {
					tableNames.add(name);
				}
			}
		}
		int size=genAll.getFile().size();
		
		if(size==0) return;
		
		System.out.println("----------------���ɶ��ʼ------------------");
		
		for (ConfigModel.GenAll.File file : genAll.getFile()) {
			if ("MultiFile".equals(file.getGenMode())) {
				for (String tableName : tableNames) {
					// �����ݿ��ж�ȡ��tableModel
					TableModel tableModel = dbHelper.getByTable(tableName);

					Map<String, Object> map = new HashMap<String, Object>();
				
					map.put("model", tableModel);
					map.put("date", new Date());

					genFile(file.getDir(), tableName + "." + file.getExtName(), file.getTemplate(),
							configModel.getCharset(), cfg, map);
					System.out.println(tableName + "." + file.getExtName() + " ���ɳɹ�!");
				}

			} else if ("SingleFile".equals(file.getGenMode())) {
				List<TableModel> models = new ArrayList<TableModel>();
				for (String tableName : tableNames) {
					// �����ݿ��ж�ȡ��tableModel
					TableModel tableModel = dbHelper.getByTable(tableName);
					models.add(tableModel);
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("models", models);
				map.put("date", new Date());
				genFile(file.getDir(), file.getFilename(), file.getTemplate(), configModel.getCharset(), cfg,map);
				System.out.println(file.getFilename() + " ���ɳɹ�!");
			}
		}
		System.out.println("----------------���ɶ�����------------------");
		
	}

	/**
	 * �����ļ�
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param templatePath
	 * @param charset
	 * @param cfg
	 * @param data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void genFile(String fileDir, String fileName, String templatePath, String charset, Configuration cfg,
			Map data) throws IOException, TemplateException {
		String path=StringUtil.combilePath(fileDir, fileName);
		//���ȱ����ļ�
		//FileHelper.backupFile(path);
		File newFile = new File(fileDir, fileName);
		if (!newFile.exists()) {
			if (!newFile.getParentFile().exists()) {
				newFile.getParentFile().mkdirs();
			}
			newFile.createNewFile();
		}
		Writer writer = new OutputStreamWriter(new FileOutputStream(newFile), charset);
		// ģ���ļ�
		Template template = cfg.getTemplate(templatePath, charset);
		// ����
		template.process(data, writer);
	}
	
	
	private void appendFile(String fileDir, String fileName, String templatePath, String charset, Configuration cfg,
			Map data,String insertTag,String startTag,String endTag) throws IOException, TemplateException
	{
		
		String path=StringUtil.combilePath(fileDir, fileName);
		File newFile = new File(fileDir, fileName);

		StringWriter out=new StringWriter();
		Template template = cfg.getTemplate(templatePath, charset);
		template.process(data, out);
		String str=out.toString();

		
		boolean exists=false;
		String content="";
		if(newFile.exists()){
			content=FileHelper.readFile(path,charset);
			if(StringUtil.isNotEmpty(startTag) && StringUtil.isNotEmpty(endTag)){
				if(StringUtil.isExist(content, startTag, endTag)){
					content=StringUtil.replace(content, startTag, endTag, str);
					exists=true;
				}
			}
		}
		//�Ѿ��滻���������˲�����
		if(!exists){
			if(StringUtil.isNotEmpty(startTag) && StringUtil.isNotEmpty(endTag)){
				str=startTag.trim() +"\r\n" + str  +"\r\n" + endTag.trim();
			}
			if(content.indexOf(insertTag)!=-1){
				String[] aryContent=FileHelper.getBySplit(content, insertTag);
				content=aryContent[0] + str + "\r\n" + insertTag + aryContent[1];
			}
			else{
				content=content + "\r\n" + str;
			}
		}
		FileHelper.writeFile(newFile, charset, content);
	}
	

	/**
	 * ��ȡDbHelper��
	 * 
	 * @param configModel
	 * @return
	 * @throws CodegenException
	 */
	private IDbHelper getDbHelper(ConfigModel configModel) throws CodegenException {
		IDbHelper dbHelper = null;
		String dbHelperClass = configModel.getDatabase().getDbHelperClass();

		try {
			dbHelper = (IDbHelper) Class.forName(dbHelperClass).newInstance();
		} catch (InstantiationException e) {
			throw new CodegenException("ָ����dbHelperClass��" + dbHelperClass
					+ "�޷�ʵ���������ܸ�����һ�������ࡢ�ӿڡ������ࡢ�������ͻ� void, ���߸���û��Ĭ�Ϲ��췽��!", e);
		} catch (IllegalAccessException e) {
			throw new CodegenException("ָ����dbHelperClass�� " + dbHelperClass + "û��Ĭ�Ϲ��췽���򲻿ɷ���!", e);
		} catch (ClassNotFoundException e) {
			throw new CodegenException("�Ҳ���ָ����dbHelperClass:" + dbHelperClass + "!", e);
		}
		dbHelper.setUrl(configModel.getDatabase().getUrl(), configModel.getDatabase().getUsername(), configModel
				.getDatabase().getPassword());
		return dbHelper;
	}

	public void execute() throws Exception {
		try{
		
			String configXml=getRootPath() + "codegenconfig.xml";
			File file=new File(configXml);
			if(!file.exists()){
				throw new CodegenException( "��ȷ�������ļ���"+configXml +"�Ƿ����!");
			}
			
			ConfigModel configModel = loadXml();
			
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(configModel.getTemplates().getBasepath()));
			
			IDbHelper dbHelper=getDbHelper(configModel);
			
			//���ɱ�
			genTableByConfig(dbHelper, configModel, cfg);
			//����ȫ����
			getAllTable(dbHelper, configModel, cfg);
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
