package org.easyframe.cxfplus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.namespace.QName;

import jef.common.log.LogUtil;
import jef.tools.ArrayUtils;
import jef.tools.StringUtils;

import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.CXFPlusServiceFactoryBean;
import org.apache.cxf.service.factory.CXFPlusServiceBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.easyframe.cxfplus.support.DefaultWsFactory;
import org.easyframe.cxfplus.support.IWebService;
import org.easyframe.cxfplus.support.ServcieLookup;
import org.easyframe.cxfplus.support.ServiceDefinition;
import org.easyframe.cxfplus.support.ServiceProcessor;
import org.easyframe.cxfplus.support.SpringServletServcieLookup;
import org.easyframe.jaxrs.FastJSONProvider;
import org.easyframe.jaxrs.IRestfulService;
import org.easyframe.jaxws.interceptors.TraceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用来发布WebService的Servlet。和Spring集成，启动时查找所有IWebService接口的服务类，自动进行发布。
 * 
 * @author jiyi
 * 
 */
public class CXFPlusServlet extends CXFNonSpringServlet {
	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	private ServcieLookup WSlookup;
	private ServcieLookup RSlookup;
	private ServiceProcessor serviceUtill;
	private boolean trace;
	
	/**
	 * 如果是SimpleMode就直接用ServerFactoryBean发布，不用JaxWs方式发布
	 */
	private boolean wsSimpleMode = false;
	private String[] wsNamePattern;
	
	
	private String restBasepath="/rest";
	private int restUseFastJson=1; //0 不使用 1 使用写 2 使用读写
	private boolean restJsonWithClassname;
	private String restBasePath;
	private String[] restNamePattern;

	public void init() throws ServletException {
		super.init();
	}

	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);
		initLookup();
		if (isRemoteMode()) {
			this.log.info("Current mode is remote work, no Jax-rs need to publish....");
			return;
		}
		initParam(sc);
		processWebservice(sc);
		processJaxRs(sc);
	}
	
	private void initLookup() {
		if (WSlookup == null) {
			WSlookup = new SpringServletServcieLookup(this.getServletContext(),IWebService.class);
		}
		if(RSlookup==null){
			RSlookup=new SpringServletServcieLookup(getServletContext(), IRestfulService.class);
		}
	}

	private void processJaxRs(ServletConfig sc) {
		log.debug("Start procesing the Restful-Services.");
		Map<Class<?>, ResourceProvider> resProviders = new HashMap<Class<?>, ResourceProvider>();
		for (ServiceDefinition def : RSlookup.getServices()) {
			if (def.getServiceClass() == null)
				continue;
			
			if (canPublish(def.getName(),restNamePattern)) {
				ServiceDefinition context=serviceUtill.processServiceDef(def);
				if (context != null) {
					resProviders.put(context.getServiceClass(), new SingletonResourceProvider(context.getServiceBean()));
				}
			}
		}
		//计算完成开始发布
		if (!resProviders.isEmpty()) {
			doPublishRestful(resProviders);
		}
	}

	public void processWebservice(ServletConfig sc) {
		/*
		 * Force use JEF's JAXB+ engine. 让JDK内置的JAXB实现查找器固定JEF实现上。目前暂不需用
		 * System.setProperty("javax.xml.bind.JAXBContext",
		 * "jef.com.sun.xml.bind.v2.ContextFactory");
		 */
		log.debug("Start processing the WebServices.");
		for (ServiceDefinition def : WSlookup.getServices()) {
			if (def.getServiceClass() == null)
				continue;
			if (canPublish(def.getName(),wsNamePattern)) {
				try {
					doPublishWebservice(serviceUtill.processServiceDef(def));
				} catch (Throwable e) {
					this.log.error("The service " + def.getServiceClass() + " can not publish as a WebService.", e);
				}
			}
		}
	}

	private void initParam(ServletConfig sc) {
		if(sc==null){
			this.trace=true;
			this.wsSimpleMode=false;
		}else{
			this.trace = StringUtils.toBoolean(sc.getInitParameter("trace"), false);
			this.wsSimpleMode = StringUtils.toBoolean(sc.getInitParameter("simpleMode"), false);
			this.wsNamePattern = StringUtils.split(sc.getInitParameter("ws-service-name"), ',');
			
			String restBasepath=sc.getInitParameter("rest-basepath");
			if(StringUtils.isNotEmpty(restBasepath)){
				this.restBasepath=restBasepath;
			}
			this.restUseFastJson=StringUtils.toInt(sc.getInitParameter("rest-use-fastjson"), 2);
			this.restJsonWithClassname=StringUtils.toBoolean(sc.getInitParameter("rest-json-withclassname"), false);
			this.restNamePattern = StringUtils.split(sc.getInitParameter("rest-service-name"), ',');
		}
		if (WSlookup instanceof SpringServletServcieLookup) {
			this.serviceUtill = ((SpringServletServcieLookup) WSlookup).getWebServiceProcessor();
		}
		if (serviceUtill == null) {
			this.serviceUtill = DefaultWsFactory.getInstance();
		}
	}

	/*
	 * 判断是不是处理客户端模式
	 */
	private boolean isRemoteMode() {
		if (WSlookup instanceof SpringServletServcieLookup) {
			return ((SpringServletServcieLookup) WSlookup).isRemoteMode();
		}
		return false;
	}

	/*
	 * 判断是否要发布
	 */
	private boolean canPublish(String name,String[] namePatterns) {
		if ((namePatterns == null) || (namePatterns.length == 0))
			return true;
		for (String key : namePatterns) {
			if (key.length() == 0)
				return true;
			if (StringUtils.matches(name, key, true)) {
				return true;
			}
		}
		return false;
	}

	private void doPublishWebservice(ServiceDefinition service) {
		if (service == null)
			return;
		Object serviceBean = service.getServiceBean();
		Class<?> serviceClass = service.getServiceClass();
		String[] packageName = StringUtils.split(serviceClass.getPackage().getName(), ".");
		ArrayUtils.reverse(packageName);
		String servName = service.getServiceClass().getSimpleName();
		if (this.wsSimpleMode) {
			ServerFactoryBean bean = new ServerFactoryBean(new CXFPlusServiceBean());
			bean.setServiceClass(serviceClass);
			bean.setServiceBean(serviceBean);
			bean.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));
			bean.setAddress(service.getPath());
			if (this.trace) {
				bean.getInInterceptors().add(new LoggingInInterceptor());
				bean.getOutInterceptors().add(new LoggingOutInterceptor());
			}
			Server server = bean.create();
			@SuppressWarnings("unused")
			org.apache.cxf.endpoint.EndpointImpl epimpl = (org.apache.cxf.endpoint.EndpointImpl) server.getEndpoint();
			// epimpl.publish();
		} else {
			CXFPlusServiceFactoryBean myFac = new CXFPlusServiceFactoryBean();

			org.apache.cxf.jaxws.EndpointImpl epimpl = new org.apache.cxf.jaxws22.EndpointImpl(BusFactory.getThreadDefaultBus(), serviceBean, new JaxWsServerFactoryBean(myFac));
			epimpl.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));

			if (this.trace) {
				epimpl.getHandlers().add(new TraceHandler());
			}
			LogUtil.show("Starting Webservice: " + service.getPath());
			epimpl.publish(service.getPath());
		}
	}
	
	private void doPublishRestful(Map<Class<?>, ResourceProvider> springResources) {
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		for (Entry<Class<?>, ResourceProvider> e : springResources.entrySet()) {
			sf.setResourceClasses(e.getKey());
			sf.setResourceProvider(e.getKey(), e.getValue());
			log.info("Publishing JAX-RS Service [{}], url=", e, restBasePath);
		}
		sf.setAddress(restBasepath);
		if(restUseFastJson==1){
			sf.setProvider(new FastJSONProvider(false,restJsonWithClassname));
		}else{
			sf.setProvider(new FastJSONProvider(true,restJsonWithClassname));
		}
		if(trace){
			sf.getInInterceptors().add(new LoggingInInterceptor());
			sf.getOutInterceptors().add(new LoggingOutInterceptor());	
		}
		sf.create();

	}
}
