<#assign package=model.variables.package>
<#assign class=model.variables.class>
<#assign classVar=model.variables.classVar>
<#assign system=vars.system>
package com.quanyu.${system}.${package}.manager;

import java.io.Serializable;
import com.quanyu.base.manager.api.Manager;
import com.quanyu.${system}.${package}.model.${class};


public interface ${class}Manager <PK extends Serializable,T extends ${class}> extends Manager<PK, T>{
	
}

