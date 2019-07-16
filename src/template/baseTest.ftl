<#assign package=model.variables.package>
<#assign class=model.variables.class>
<#assign classVar=model.variables.classVar>
<#assign system=vars.system>
<#assign baseClass=model.variables.baseClass>
package com.quanyu.${system}.${package}.test;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;

import com.quanyu.base.db.api.IdGenerator;
import com.quanyu.base.test.BaseTestCase;

/**
 * 测试基类，。
 * 其下的测试类均继承该子类
 * @author ${vars.developer}
 *
 */
@ContextConfiguration({"classpath:conf/x5-examples.xml"})
public class ${baseClass}BaseTest extends BaseTestCase{
	
	@Resource
    protected IdGenerator idGenerator;
}
