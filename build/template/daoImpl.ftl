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
package com.quanyu.${system}.persistence.dao.impl;

<#if sub?exists && sub>
import java.util.HashMap;
import java.util.List;
import java.util.Map;
</#if>
import org.springframework.stereotype.Repository;

import com.quanyu.base.db.impl.MyBatisDaoImpl;
import com.quanyu.${system}.persistence.dao.${class}Dao;
import com.quanyu.${system}.persistence.model.${class};

/**
 * 
 * <pre> 
 * 描述：${comment} DAO实现类
 * 构建组：efssp-platform
 <#if vars.developer?exists>
 * 作者:${vars.developer}
 * 邮箱:${vars.email}
 </#if>
 * 日期:${date?string("yyyy-MM-dd HH:mm:ss")}
 * 版权：${vars.company}
 * </pre>
 */
@Repository
public class ${class}DaoImpl extends MyBatisDaoImpl<${pkType}, ${class}> implements ${class}Dao{

    @Override
    public String getNamespace() {
        return ${class}.class.getName();
    }
<#if sub?exists && sub>
	/**
	 * 根据外键获取子表明细列表
	 * @param ${foreignKey}
	 * @return
	 */
	public List<${class}> getByMainId(${fkType} ${foreignKey}) {
		Map<String,Object>params=new HashMap<String, Object>();
		params.put("${foreignKey}", ${foreignKey});
		return this.getByKey("get${class}List", params);
	}
	
	/**
	 * 根据外键删除子表记录
	 * @param ${foreignKey}
	 * @return
	 */
	public void delByMainId(${fkType} ${foreignKey}) {
		Map<String,Object>params=new HashMap<String, Object>();
		params.put("${foreignKey}", ${foreignKey});
		this.deleteByKey("delByMainId", params);
	}
	
</#if>	
	
}

