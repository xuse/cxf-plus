package com.googlecode.jef.ws;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.namespace.QName;

import jef.common.log.LogUtil;
import jef.tools.ArrayUtils;
import jef.tools.Assert;
import jef.tools.StringUtils;
import jef.tools.reflect.ClassWrapper;

import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.CXFPlusServiceFactoryBean;
import org.apache.cxf.service.factory.CXFPlusServiceBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.jef.ws.interceptors.TraceHandler;

/**
 * 用来发布WebService的Servlet。和Spring集成，启动时查找所有IWebService接口的服务类，自动进行发布。
 * 
 * @author jiyi
 *
 */
public class WSServlet extends CXFNonSpringServlet {
	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
	
	private WebServiceFactory serviceUtill;
	private boolean trace;
	/**
	 * 如果是SimpleMode就直接用ServerFactoryBean发布，不用JaxWs方式发布
	 */
	private boolean simpleMode = false;
	private String[] namePattern;
	private Logger log = LoggerFactory.getLogger(getClass());

	public void init() throws ServletException {
		super.init();
	}

	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);
        /*
         * Force use JEF's JAXB+ engine. 让JDK内置的JAXB实现查找器固定JEF实现上。目前暂不需用
         * System.setProperty("javax.xml.bind.JAXBContext", "jef.com.sun.xml.bind.v2.ContextFactory");
         */
		if (this.context == null) {
			this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(super.getServletContext());
			Assert.notNull(this.context, "Spring Context not found in servletContext...");
			if(isRemoteMode()){
				this.log.info("Current mode is remote work, no webservice need to publish....");
				return;
			}
			initParam(sc);
		}

		//获取要发布的服务
		Map<String, IWebService> beans = this.context.getBeansOfType(IWebService.class);
		for (String key : beans.keySet())
			if (canPublish(key)) {
				IWebService impl = (IWebService) beans.get(key);
				Class<?>[] intfs = new ClassWrapper(impl.getClass()).getInterfaces();
				if (intfs.length==0)
					continue;
				//寻找服务的发布接口
				Class<?> servClass = null;
				for (Class<?> clz : intfs) {
					if (IWebService.class.isAssignableFrom(clz)) {
						servClass = clz;
						break;
					}
				}
				if (servClass == null) {
					System.out.println("Service Class Not found... " + StringUtils.join(intfs, ","));
				} else {
					String url = "/" + key;
					String servName = servClass.getSimpleName();
					try {
						publish(url, servName, this.serviceUtill.createServerBean(impl, servClass));
					} catch (Throwable e) {
						this.log.error("The service " + servClass + " can not publish as a WebService.",e);
					}
				}
			}
	}

	private void initParam(ServletConfig sc) {
		String trace = sc.getInitParameter("trace");
		this.trace = StringUtils.toBoolean(trace, Boolean.valueOf(false));
		String simple = sc.getInitParameter("simpleMode");
		this.simpleMode = StringUtils.toBoolean(simple, Boolean.valueOf(false));
		this.namePattern = StringUtils.split(sc.getInitParameter("service-name"), ',');
		
		try{
			this.serviceUtill = ((WebServiceFactory) this.context.getBean(WebServiceFactory.class));	
		}catch(Exception e){
			this.serviceUtill=DefaultWsFactory.getInstance();
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
		return obj!=null;
	}
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

	public void publish(String url, String servName, WsContext service) {
		if (service == null)
			return;
		Object serviceBean = service.getServiceBean();
		Class<?> serviceClass = service.getServiceClass();
		String[] packageName = StringUtils.split(serviceClass.getPackage().getName(), ".");
		ArrayUtils.reverse(packageName);
		if (this.simpleMode) {
			ServerFactoryBean bean = new ServerFactoryBean(new CXFPlusServiceBean());
			bean.setServiceClass(serviceClass);
			bean.setServiceBean(serviceBean);
			bean.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));
			bean.setAddress(url);
			if(this.trace){
				bean.getInInterceptors().add(new LoggingInInterceptor());
				bean.getOutInterceptors().add(new LoggingOutInterceptor());
			}
			Server server = bean.create();
			org.apache.cxf.endpoint.EndpointImpl epimpl = (org.apache.cxf.endpoint.EndpointImpl) server.getEndpoint();
//			epimpl.publish();
		} else {
			CXFPlusServiceFactoryBean myFac=new CXFPlusServiceFactoryBean();
			
			org.apache.cxf.jaxws.EndpointImpl epimpl = new org.apache.cxf.jaxws22.EndpointImpl(BusFactory.getThreadDefaultBus(),serviceBean,new JaxWsServerFactoryBean(myFac));
			epimpl.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));

			if (this.trace) {
				epimpl.getHandlers().add(new TraceHandler());
			}
			LogUtil.show("Starting Webservice: " + url);
			epimpl.publish(url);
		}
	}
}
