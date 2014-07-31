package com.googlecode.jef.ws;

import jef.tools.Assert;
import jef.tools.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;



public class JaxWsProxyBeanFactory implements FactoryBean<Object>,InitializingBean{
	private static Logger log=LoggerFactory.getLogger(JaxWsProxyBeanFactory.class);
	
	private String serviceInterface;
	private String url;
	private String host;
	private String port;
	private String contextPath;
	
	private Class<?> clz;
	private Object service;
	private WebServiceFactory factory;
	
	@Autowired(required=false)
	public void setFactory(WebServiceFactory factory) {
		this.factory = factory;
	}

	public void setServiceInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Object getObject() throws Exception {
		if(service==null){
			WebServiceFactory factory=this.factory;
			if(factory==null){
				factory=DefaultWsFactory.getInstance(false);
			}
			log.info("Initializing remote bean: {}",url);
			if(!url.startsWith("http://")){
				String server=host;
				int port=StringUtils.toInt(this.port, 80);
				String context=this.contextPath;
				if(!context.startsWith("/"))context="/".concat(context);
				if(!url.startsWith("/"))url="/".concat(url);
				if(port==80){
					url=StringUtils.concat("http://",server,context,url);
				}else{
					url=StringUtils.concat("http://",server,":",String.valueOf(port),context,url);
				}
			}
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if( loader==null ){
				loader = JaxWsProxyBeanFactory.class.getClassLoader();
			}
			this.clz=loader.loadClass(serviceInterface);
			this.service=factory.createClientProxy(url, clz);
		}
		log.debug("Return remote service bean {}" , service);
		return service;
	}

	public Class<?> getObjectType() {
		return clz;
	}

	public boolean isSingleton() {
		return false;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(factory);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	
}
