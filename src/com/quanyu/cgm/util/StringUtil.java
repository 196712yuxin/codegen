package com.quanyu.cgm.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quanyu.cgm.exception.CodegenException;

public class StringUtil {
	
	public static boolean isEmpty(String str)
	{
		if(str==null) return true;
		if(str.trim().equals("")) return true;
		return false;
	}
	
	public static boolean isNotEmpty(String str)
	{
		
		return !isEmpty(str);
	}
	
	/**
	 * �滻������<br>
	 * <pre>
	 * ʹ�÷������£�
	 * String template="com/quanyu/{path}/model/{class}";
	 * Map<String,String> map=new HashMap<String,String>();
	 * map.put("path","platform");
	 * map.put("class","Role");
	 * String tmp=replaceVariable(template,map);
	 * </pre>
	 * @param template
	 * @param map
	 * @return
	 * @throws CodegenException 
	 */
	public static String replaceVariable(String template,Map<String,String> map) throws CodegenException
	{
		Pattern regex = Pattern.compile("\\{(.*?)\\}");
		Matcher regexMatcher = regex.matcher(template);
		while (regexMatcher.find()) {
			String key=regexMatcher.group(1);
			String toReplace=regexMatcher.group(0);
			String value=(String)map.get(key);
			if(value!=null)
				template=template.replace(toReplace, value);
			else
				throw new CodegenException("û���ҵ�["+ key +"]��Ӧ�ı���ֵ��������������!");
		}  
		
		return template;
	}
	
	/**
	 * �滻�����
	 * @param template
	 * @param tableName
	 * @return
	 */
	public static String replaceVariable(String template,String tableName) 
	{
		Pattern regex = Pattern.compile("\\{(.*?)\\}");
		Matcher regexMatcher = regex.matcher(template);
		if (regexMatcher.find()) {
			String toReplace=regexMatcher.group(0);
			template=template.replace(toReplace, tableName);
		}
		return template;
	}
	
	/**
	 * ȥ��ǰ����ַ�
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trimPrefix(String toTrim,String trimStr)
	{
		while(toTrim.startsWith(trimStr))
		{
			toTrim=toTrim.substring(trimStr.length());
		}
		return toTrim;
	}
	
	/**
	 * ɾ��������ַ�
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trimSufffix(String toTrim,String trimStr)
	{
		while(toTrim.endsWith(trimStr))
		{
			toTrim=toTrim.substring(0,toTrim.length()-trimStr.length());
		}
		return toTrim;
	}
	
	/**
	 * ɾ��ָ�����ַ�
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trim(String toTrim,String trimStr)
	{
		return trimSufffix(trimPrefix(toTrim, trimStr), trimStr);
	}
	
	public static String combilePath(String baseDir,String dir)
	{
		baseDir=trimSufffix(baseDir, File.separator) ;
		dir=trimPrefix(dir,File.separator);
		
		return baseDir + File.separator + dir;
	}
	
	/**
	 * ʹ���ַ����滻����
	 * @param content ����
	 * @param startTag ��ʼ��ǩ
	 * @param endTag ������ǩ
	 * @param repalceWith ʹ���滻
	 * @return
	 */
	public static String replace(String content,String startTag,String endTag,String repalceWith)
	{
		String tmp=content.toLowerCase();
		String tmpStartTag=startTag.toLowerCase();
		String tmpEndTag=endTag.toLowerCase();
		
		
		StringBuffer sb=new StringBuffer();
		int beginIndex=tmp.indexOf(tmpStartTag);
		int endIndex=tmp.indexOf(tmpEndTag);
		while(beginIndex!=-1 && endIndex!=-1 && beginIndex<endIndex)
		{
			String pre=content.substring(0,tmp.indexOf(tmpStartTag)+tmpStartTag.length());
			String suffix=content.substring(tmp.indexOf(tmpEndTag));
			
			tmp=suffix.toLowerCase();
			content=suffix.substring(endTag.length());
			
			beginIndex=tmp.indexOf(tmpStartTag);
			endIndex=tmp.indexOf(tmpEndTag);
			String replaced=pre + "\r\n" +  repalceWith  +"\r\n" + endTag;
			sb.append(replaced);
		}
		sb.append(content);
		return sb.toString();
	}
	
	/**
	 * �ж�ָ���������Ƿ����
	 * @param content ����
	 * @param begin ��ʼ��ǩ
	 * @param end ������ǩ
	 * @return
	 */
	public static boolean isExist(String content,String begin,String end)
	{
		String tmp=content.toLowerCase();
		begin=begin.toLowerCase();
		end=end.toLowerCase();
		int beginIndex=tmp.indexOf(begin);
		int endIndex=tmp.indexOf(end);
		if(beginIndex!=-1  && endIndex!=-1 && beginIndex<endIndex)
			return true;
		return false;
	}
	
	/**
	 * ��ȡ�ַ����е� ָ�����ִ� �м�� �ַ���
	 * @param start
	 * @param end
	 * @return
	 */
	public static String subString(String content,String start,String end){
		String str=content;
		if(content.indexOf(start)!=-1){
			if(content.indexOf(end)!=-1){
				str=content.substring(content.indexOf(start)+start.length(),content.lastIndexOf(end));
			}else{
				str=content.substring(content.indexOf(start)+start.length());
			}
		}
		
		return str;
	}
	
	/**
	 * �ַ�������ĸСд
	 * @param str
	 * @return
	 */
	public static String toFirstLowerCase(String str){
		Character head=str.charAt(0);
		String lower=head.toString().toLowerCase();
		return str.replaceFirst(head.toString(),lower);
	}
	
	
	public static void main(String[] args) throws CodegenException
	{
		toFirstLowerCase("ABC");
	}
	

}
