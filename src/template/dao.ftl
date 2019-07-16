<#import "function.ftl" as func>
<#assign package=model.variables.package>
<#assign class=model.variables.class>
<#assign classVar=model.variables.classVar>
<#assign comment=model.tabComment>
<#assign system=vars.system>
<#assign sub=model.sub>
<#assign foreignKey=func.convertUnderLine(model.foreignKey)>
<#assign pkType=func.getPkType(model)>
<#assign fkType=func.getFkType(model)>
package com.quanyu.${system}.persistence.dao;
<#if sub?exists && sub>
import java.util.HashMap;
import java.util.List;
import java.util.Map;
</#if>
import com.quanyu.base.db.api.Dao;
import com.quanyu.${system}.persistence.model.${class};

/**
 * 
 * <pre> 
 * 描述：${comment} DAO接口
 * 构建组：efssp-platform
 <#if vars.developer?exists>
 * 作者:${vars.developer}
 * 邮箱:${vars.email}
 </#if>
 * 日期:${date?string("yyyy-MM-dd HH:mm:ss")}
 * 版权：${vars.company}
 * </pre>
 */
public interface ${class}Dao extends Dao<${pkType}, ${class}> {
	<#if sub?exists && sub>
	/**
	 * 根据外键获取子表明细列表
	 * @param ${foreignKey}
	 * @return
	 */
	public List<${class}> getByMainId(${fkType} ${foreignKey});
	
	/**
	 * 根据外键删除子表记录
	 * @param ${foreignKey}
	 * @return
	 */
	public void delByMainId(${fkType} ${foreignKey});
	
	</#if>	
}
