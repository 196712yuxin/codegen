<#import "function.ftl" as func>
<#assign package=model.variables.package>
<#assign comment=model.tabComment>
<#assign class=model.variables.class>
<#assign classVar=model.variables.classVar>
<#assign system=vars.system>
<#assign pkType=func.getPkType(model)>
package com.quanyu.${system}.persistence.manager;

import com.quanyu.base.manager.api.Manager;
import com.quanyu.${system}.persistence.model.${class};

/**
 * 
 * <pre> 
 * 描述：${comment} 处理接口
 * 构建组：efssp-platform
 <#if vars.developer?exists>
 * 作者:${vars.developer}
 * 邮箱:${vars.email}
 </#if>
 * 日期:${date?string("yyyy-MM-dd HH:mm:ss")}
 * 版权：${vars.company}
 * </pre>
 */
public interface ${class}Manager extends Manager<${pkType}, ${class}>{
	
}
