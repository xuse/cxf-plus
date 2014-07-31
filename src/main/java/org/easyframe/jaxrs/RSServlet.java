package org.easyframe.jaxrs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;

import jef.tools.Assert;
import jef.tools.StringUtils;
import jef.tools.reflect.ClassWrapper;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.jef.ws.DefaultWsFactory;
import com.googlecode.jef.ws.WebServiceFactory;
import com.googlecode.jef.ws.WsContext;

public class RSServlet extends CXFNonSpringServlet {
	private ApplicationContext context;
	private boolean trace;
	private int useFastJson=1; //0 不使用 1 使用写 2 使用读写
	private String basePath;
	private String[] namePattern;
	private WebServiceFactory serviceUtill;
	private Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * 判断是否要发布
	 */
	private boolean canPublish(String name) {
		if ((this.namePattern == null) || (this.namePattern.length == 0))
			return true;
		for (String key : this.namePattern) {
			if (key.length() == 0)
				return true;
			if (StringUtils.matches(name, key, true)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);
		Map<Class<?>, ResourceProvider> resources = loadFromSpring(sc);
		if (!resources.isEmpty()) {
			publish(resources);
		}
	}

	private void publish(Map<Class<?>, ResourceProvider> springResources) {
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		for (Entry<Class<?>, ResourceProvider> e : springResources.entrySet()) {
			sf.setResourceClasses(e.getKey());
			sf.setResourceProvider(e.getKey(), e.getValue());
			log.info("Publishing JAX-RS Service [{}]", e);
		}
		sf.setAddress(basePath);
		if(useFastJson==1){
			sf.setProvider(new FastJSONProvider(false));
		}else{
			sf.setProvider(new FastJSONProvider(true));
		}
		if(trace){
			sf.getInInterceptors().add(new LoggingInInterceptor());
			sf.getOutInterceptors().add(new LoggingOutInterceptor());	
		}
		
//		BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
//		JAXRSBindingFactory factory = new JAXRSBindingFactory();
//		factory.setBus(sf.getBus());
//		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);
//		Map<Object,Object> map=new HashMap<Object,Object>();
//		map.put("xml", "application/xml");
//		map.put("json", "application/json");
//		sf.setExtensionMappings(map);
		sf.create();

	}

	protected Map<Class<?>, ResourceProvider> loadFromSpring(ServletConfig sc) {
		if (this.context == null) {
			this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(super.getServletContext());
			Assert.notNull(this.context, "Spring Context not found in servletContext...");
			if (isRemoteMode()) {
				this.log.info("Current mode is remote work, no webservice need to publish....");
				return Collections.emptyMap();
			}
			initParam(sc);
		}

		// 获取要发布的服务
		Map<String, IRestfulService> beans = this.context.getBeansOfType(IRestfulService.class);
		Map<Class<?>, ResourceProvider> springResources = new HashMap<Class<?>, ResourceProvider>();
		for (String key : beans.keySet()) {
			if (canPublish(key)) {
				IRestfulService impl = (IRestfulService) beans.get(key);
				Class<?>[] intfs = new ClassWrapper(impl.getClass()).getInterfaces();
				// 寻找服务的发布接口
				Class<?> sei = null;
				for (Class<?> clz : intfs) {
					if (clz == IRestfulService.class) {
						sei = impl.getClass();
						break;
					}
					if (IRestfulService.class.isAssignableFrom(clz)) {
						sei = clz;
						break;
					}
				}
				if(sei!=null){
					WsContext context=serviceUtill.createServerBean(impl, sei);
					// 找到接口
					if (context != null) {
						springResources.put(context.getServiceClass(), new SingletonResourceProvider(context.getServiceBean()));
					}
	
				}
			}
		}
		return springResources;
	}

	private void initParam(ServletConfig sc) {
		String trace = sc.getInitParameter("trace");
		this.trace = StringUtils.toBoolean(trace, Boolean.valueOf(false));
		this.basePath = sc.getInitParameter("base-path");
		if (StringUtils.isEmpty(basePath)) {
			basePath = "/";
		} else if (!basePath.startsWith("/")) {
			basePath = "/" + basePath;
		}
		String useFastJson=sc.getInitParameter("use-fastjson");
		if("2".equals(useFastJson)){
			this.useFastJson=2;
		}else if("0".equals(useFastJson)){
			this.useFastJson=0;
		}else{
			this.useFastJson=1;
		}
		this.namePattern = StringUtils.split(sc.getInitParameter("service-name"), ',');
		try {
			this.serviceUtill = ((WebServiceFactory) this.context.getBean(WebServiceFactory.class));
		} catch (Exception e) {
			this.serviceUtill = DefaultWsFactory.getInstance();
		}
	}

	/*
	 * 判断是不是处理客户端模式
	 */
	private boolean isRemoteMode() {
		Object obj = null;
		try {
			obj = this.context.getBean("_REMOTE_MODE");
		} catch (Exception e) {
		}
		return obj != null;
	}
}
