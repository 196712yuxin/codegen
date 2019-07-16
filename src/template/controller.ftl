<#import "function.ftl" as func>
<#assign package=model.variables.package>
<#assign class=model.variables.class>
<#assign system=vars.system>
<#assign comment=model.tabComment>
<#assign subtables=model.subTableList>
<#assign classVar=model.variables.classVar>
<#assign pk=func.getPk(model) >
<#assign pkVar=func.convertUnderLine(pk) >
<#assign pkType=func.getPkType(model)>
package com.quanyu.${system}.${package}.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.quanyu.base.api.model.ResultMessage;
import com.quanyu.base.api.query.QueryFilter;
import com.quanyu.base.core.util.string.StringUtil;
import com.quanyu.base.db.id.UniqueIdUtil;
import com.quanyu.base.db.mybatis.domain.PageList;
import com.quanyu.${system}.${package}.manager.${class}Manager;
import com.quanyu.${system}.${package}.model.${class};
import com.quanyu.web.controller.GenericController;
import com.quanyu.web.json.PageJson;
import com.quanyu.sys.util.RequestUtil;

/**
 * 
 * <pre> 
 * 描述：${comment} 控制器类
 * 构建组：efssp-platform
 <#if vars.developer?exists>
 * 作者:${vars.developer}
 * 邮箱:${vars.email}
 </#if>
 * 日期:${date?string("yyyy-MM-dd HH:mm:ss")}
 * 版权：${vars.company}
 * </pre>
 */
@Controller
@RequestMapping("/${system}/${package}/${classVar}/")
public class ${class}Controller extends GenericController{
	@Resource
	${class}Manager ${classVar}Manager;
	
	/**
	 * ${comment}列表(分页条件查询)数据
	 * @param request
	 * @param reponse
	 * @return
	 * @throws Exception 
	 * PageJson
	 * @exception 
	 */
	@RequestMapping("listJson")
	public @ResponseBody PageJson listJson(HttpServletRequest request,HttpServletResponse reponse) throws Exception{
		QueryFilter queryFilter=getQuerFilter(request);
		PageList<${class}> ${classVar}List=(PageList<${class}>)${classVar}Manager.query(queryFilter);
		return new PageJson(${classVar}List);
	}
	
	/**
	 * 编辑${comment}信息页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * ModelAndView
	 * @exception 
	 */
	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String preUrl= RequestUtil.getPrePage(request);
		${pkType} ${pkVar}=RequestUtil.get${pkType}(request, "${pkVar}");
		${class} ${classVar}=null;
		if(StringUtil.isNotEmpty(${pkVar})){
			${classVar}=${classVar}Manager.get(${pkVar});
		}
		return getAutoView().addObject("${classVar}", ${classVar}).addObject("returnUrl", preUrl);
	}
	
	/**
	 * ${comment}明细页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * ModelAndView
	 * @exception 
	 * @since  1.0.0
	 */
	@RequestMapping("get")
	public ModelAndView get(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String preUrl= RequestUtil.getPrePage(request);
		${pkType} ${pkVar}=RequestUtil.get${pkType}(request, "${pkVar}");
		${class} ${classVar}=null;
		if(StringUtil.isNotEmpty(${pkVar})){
			${classVar}=${classVar}Manager.get(${pkVar});
		}
		return getAutoView().addObject("${classVar}", ${classVar}).addObject("returnUrl", preUrl);
	}
	
	/**
	 * 保存${comment}信息
	 * @param request
	 * @param response
	 * @param ${classVar}
	 * @throws Exception 
	 * void
	 * @exception 
	 */
	@RequestMapping("save")
	public void save(HttpServletRequest request,HttpServletResponse response,${class} ${classVar}) throws Exception{
		String resultMsg=null;
		${pkType} ${pkVar}=${classVar}.get${pkVar?cap_first}();
		try {
			if(StringUtil.isEmpty(${pkVar})){
				${classVar}.setId(UniqueIdUtil.getSuid());
				${classVar}Manager.create(${classVar});
				resultMsg="添加${comment}成功";
			}else{
				${classVar}Manager.update(${classVar});
				resultMsg="更新${comment}成功";
			}
			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.SUCCESS);
		} catch (Exception e) {
			resultMsg="对${comment}操作失败";
			writeResultMessage(response.getWriter(),resultMsg,e.getMessage(),ResultMessage.FAIL);
		}
	}
	
	/**
	 * 批量删除${comment}记录
	 * @param request
	 * @param response
	 * @throws Exception 
	 * void
	 * @exception 
	 */
	@RequestMapping("remove")
	public void remove(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ResultMessage message=null;
		try {
			${pkType}[] aryIds=RequestUtil.get${pkType}AryByStr(request, "${pkVar}");
			${classVar}Manager.removeByIds(aryIds);
			message=new ResultMessage(ResultMessage.SUCCESS, "删除${comment}成功");
		} catch (Exception e) {
			message=new ResultMessage(ResultMessage.FAIL, "删除${comment}失败");
		}
		writeResultMessage(response.getWriter(), message);
	}
}
