package org.easyframe.cxfplus.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import jef.tools.Assert;
import jef.tools.reflect.ClassWrapper;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringServletServcieLookup implements ServcieLookup {
	private ServletContext sc;
	private ApplicationContext applicationContext;
	private Class<?> lookupInterface;

	public SpringServletServcieLookup(ServletContext sc,Class<?> lookupInterface) {
		this.sc = sc;
		this.lookupInterface=lookupInterface;
	}

	public List<ServiceDefinition> getServices() {
		if(applicationContext==null){
			initContext();
		}
		//获取要发布的服务
		List<ServiceDefinition> result=new ArrayList<ServiceDefinition>();
		
		Map<String, ?> beans = applicationContext.getBeansOfType(lookupInterface);
		for (String key : beans.keySet()){
			Object impl = beans.get(key);
			
			// 寻找服务的发布接口
			Class<?>[] intfs = new ClassWrapper(impl.getClass()).getInterfaces();
			if (intfs.length == 0)
				continue;
			Class<?> servClass = null;
			for (Class<?> clz : intfs) {
				if (lookupInterface.isAssignableFrom(clz)) {
					servClass = clz;
					break;
				}
			}
			if(servClass!=null){
				result.add(new ServiceDefinition(key, servClass,impl));
			}
		}
		return result;
	}

	private void initContext() {
		this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		Assert.notNull(this.applicationContext, "Spring Context not found in servletContext...");
	}

	public boolean isRemoteMode() {
		if(applicationContext==null){
			initContext();
		}
		try {
			return applicationContext.getBean("_REMOTE_MODE")!=null;
		} catch (Exception e) {
		}
		return false;
	}

	public ServiceProcessor getWebServiceProcessor() {
		if(applicationContext==null){
			initContext();
		}
		try{
			return ((ServiceProcessor) applicationContext.getBean(ServiceProcessor.class));	
		}catch(Exception e){
			return null;
		}
	}
}
