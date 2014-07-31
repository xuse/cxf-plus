package com.googlecode.jef.ws;

import java.util.Map;

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
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.CXFPlusServiceFactoryBean;
import org.apache.cxf.service.factory.CXFPlusServiceBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jef.ws.interceptors.TraceHandler;

/**
 * 一个简单的WebSerive加载器。
 * 
 * 用户只需要事先调用
 * 
 * <pre>
 * <code>
 * DefaultWsFactory.getInstance().regist("serviceA", new MyWsTestImpl(), MyWsTest.class);
 * </code>
 * </pre>
 * 
 * 这样，将服务类和服务接口注册，即可在Servlet被访问时发布成WebService
 * 
 * @author jiyi
 * 
 */
public class WSNoSpringServlet extends CXFNonSpringServlet {
	private static final long serialVersionUID = 1L;
	private DefaultWsFactory serviceUtill = DefaultWsFactory.getInstance();
	private boolean trace;
	/**
	 * 如果是SimpleMode就直接用ServerFactoryBean发布，不用JaxWs方式发布
	 */
	private boolean simpleMode = false;

	private Logger log = LoggerFactory.getLogger(getClass());

	public void init() throws ServletException {
		super.init();
	}

	protected void loadBus(ServletConfig sc) {
		initParam(sc);
		super.loadBus(sc);
		for (Map.Entry<String, WsContext> wsc : this.serviceUtill.getRegistedService().entrySet()) {
			String key = (String) wsc.getKey();
			IWebService impl = (IWebService) ((WsContext) wsc.getValue()).getServiceBean();
			Class<?> servClass = ((WsContext) wsc.getValue()).getServiceClass();
			String url = "/" + key;
			String servName = servClass.getSimpleName();
			try {
				publish(url, servName, this.serviceUtill.createServerBean(impl, servClass));
			} catch (Throwable e) {
				String msg = StringUtils.exceptionStack(e, new String[0]);
				this.log.error("The service " + servClass + " can not publish as a WebService:" + msg);
			}
		}
	}

	private void initParam(ServletConfig sc) {
		this.trace = StringUtils.toBoolean(sc.getInitParameter("trace"), Boolean.valueOf(false));
		this.simpleMode = StringUtils.toBoolean(sc.getInitParameter("simpleMode"), Boolean.valueOf(false));
		this.serviceUtill.init();
	}

	public void publish(String url, String servName, WsContext service) {
		if (service == null)
			return;
		Object serviceBean = service.getServiceBean();
		Class<?> serviceClass = service.getServiceClass();
		String[] packageName = StringUtils.split(serviceClass.getPackage().getName(), ".");
		ArrayUtils.reverse(packageName);
		if (this.simpleMode) {
			ServerFactoryBean sf = new ServerFactoryBean(new CXFPlusServiceBean());
			sf.setServiceClass(serviceClass);
			sf.setServiceBean(serviceBean);
			sf.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));
			sf.setAddress(url);
			if(this.trace){
				sf.getInInterceptors().add(new LoggingInInterceptor());
				sf.getOutInterceptors().add(new LoggingOutInterceptor());
			}
			Server server = sf.create();
		} else {
			CXFPlusServiceFactoryBean myFac = new CXFPlusServiceFactoryBean();
			org.apache.cxf.jaxws.EndpointImpl epimpl = new org.apache.cxf.jaxws22.EndpointImpl(BusFactory.getThreadDefaultBus(), serviceBean, new JaxWsServerFactoryBean(myFac));
			epimpl.setServiceName(new QName("http://" + StringUtils.join(packageName, ".") + "/", servName));

			if (this.trace) {
				epimpl.getHandlers().add(new TraceHandler());
			}

			LogUtil.show("Starting Webservice: " + url);
			epimpl.publish(url);
		}
	}
}
