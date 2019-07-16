<#import "function.ftl" as func>
<#assign package=model.variables.package>
<#assign class=model.variables.class>
<#assign classVar=model.variables.classVar>
<#assign system=vars.system>
<#assign comment=model.tabComment>
<#assign subtables=model.subTableList>
<#assign pk=func.getPk(model) >
<#assign pkVar=func.convertUnderLine(pk) >
<#assign pkType=func.getPkType(model)>
<#assign fkType=func.getFkType(model)>
package com.quanyu.${system}.persistence.manager.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
<#if subtables?exists && subtables?size != 0>
import java.util.List;
<#list subtables as subtable>
import com.quanyu.${system}.persistence.dao.${subtable.variables.class}Dao;
import com.quanyu.${system}.persistence.model.${subtable.variables.class};
</#list>
</#if>
import com.quanyu.base.db.api.Dao;
import com.quanyu.base.manager.impl.AbstractManagerImpl;
import com.quanyu.${system}.persistence.dao.${class}Dao;
import com.quanyu.${system}.persistence.model.${class};
import com.quanyu.${system}.persistence.manager.${class}Manager;

/**
 * 
 * <pre> 
 * 描述：${comment} 处理实现类
 * 构建组：efssp-platform
 <#if vars.developer?exists>
 * 作者:${vars.developer}
 * 邮箱:${vars.email}
 </#if>
 * 日期:${date?string("yyyy-MM-dd HH:mm:ss")}
 * 版权：${vars.company}
 * </pre>
 */
@Service("${classVar}Manager")
public class ${class}ManagerImpl extends AbstractManagerImpl<${pkType}, ${class}> implements ${class}Manager{
	@Resource
	${class}Dao ${classVar}Dao;
	<#if subtables?exists && subtables?size != 0>
		<#list subtables as table>
	@Resource
	${table.variables.class}Dao ${table.variables.classVar}Dao;
		</#list>
	</#if>
	@Override
	protected Dao<${pkType}, ${class}> getDao() {
		return ${classVar}Dao;
	}
<#if subtables?exists && subtables?size != 0>
	/**
	 * 创建实体包含子表实体
	 */
	public void create(${class} ${classVar}){
    	super.create(${classVar});
    	<#list subtables as subtable>
    	<#assign subclass=subtable.variables.class>
    	<#assign subclassvar=subtable.variables.classVar>
    	String pk=${classVar}.get${pkVar?cap_first}();
    	
    	List<${subclass}> ${subclassvar}List=${classVar}.get${subclass}List();
    	
    	for(${subclass} ${subclassvar}:${subclassvar}List){
    		${subclassvar}.set${func.convertUnderLine(subtable.foreignKey)?cap_first}(pk);
    		${subclassvar}Dao.create(${subclassvar});
    	}
    	</#list>
    }
	
	/**
	 * 删除记录包含子表记录
	 */
	public void remove(${pkType} entityId){
		super.remove(entityId);
		<#list subtables as subtable>
    	<#assign subclass=subtable.variables.class>
    	<#assign subclassvar=subtable.variables.classVar>
    	${subclassvar}Dao.delByMainId(entityId);
    	</#list>
	}
	
	/**
	 * 批量删除包含子表记录
	 */
	public void removeByIds(${pkType}[] entityIds){
		for(${pkType} id:entityIds){
			this.remove(id);
		}
	}
    
	/**
	 * 获取实体
	 */
    public ${class} get(${pkType} entityId){
    	${class} ${classVar}=super.get(entityId);
    	<#list subtables as subtable>
    	<#assign subclass=subtable.variables.class>
    	<#assign subclassvar=subtable.variables.classVar>
    	List<${subclass}> ${subclassvar}List=${subclassvar}Dao.getByMainId(entityId);
    	${classVar}.set${subclass}List(${subclassvar}List);
    	</#list>
    	return ${classVar};
    }
    
    /**
     * 更新实体同时更新子表记录
     */
    public void update(${class} ${classVar}){
    	super.update(${classVar});
    	String id=${classVar}.get${pkVar?cap_first}();
    	<#list subtables as subtable>
    	<#assign subclass=subtable.variables.class>
    	<#assign subclassvar=subtable.variables.classVar>
    	${subclassvar}Dao.delByMainId(id);
    	List<${subclass}> ${subclassvar}List=${classVar}.get${subclass}List();
    	for(${subclass} ${subclassvar}:${subclassvar}List){
    		${subclassvar}.set${func.convertUnderLine(subtable.foreignKey)?cap_first}(id);
    		${subclassvar}Dao.create(${subclassvar});
    	}
    	</#list>
    }
	
	</#if>
}
