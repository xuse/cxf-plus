package com.googlecode.jef.ws;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.CXFPlusClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.service.factory.CXFPlusServiceBean;

import com.googlecode.jef.ws.interceptors.TraceHandler;

/**
 * 简单的WsFactory实现。
 * @author jiyi
 *
 */
public class DefaultWsFactory implements WebServiceFactory {
	private static DefaultWsFactory instance = new DefaultWsFactory();
	private final Map<String, WsContext> data = new HashMap<String, WsContext>();
	private boolean trace;

	public static DefaultWsFactory getInstance() {
		return instance;
	}
	
	public static DefaultWsFactory getInstance(boolean trace) {
		instance.trace=trace;
		return instance;
	}

	public WsContext createServerBean(Object service, Class<?> interfaceClz) {
		return new WsContext(service, interfaceClz);
	}

	public Map<String, WsContext> getRegistedService() {
		return Collections.unmodifiableMap(this.data);
	}
	public void register(String name, IWebService service, Class<?> interfaceClz) {
		this.data.put(name, new WsContext(service, interfaceClz));
	}
	@Deprecated
	public void regist(String name, IWebService service, Class<?> interfaceClz) {
		register(name,service,interfaceClz);
	}

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public <T> T createClientProxy(String url, Class<T> intf) {
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean(new CXFPlusClientFactoryBean());
		factoryBean.setAddress(url);
		factoryBean.setServiceClass(intf);
		if(trace)
			factoryBean.getHandlers().add(new TraceHandler());
		Object wsBean = factoryBean.create();
//		//设置超时
//		Client client = ClientProxy.getClient(wsBean);
//		if (client != null && (client.getConduit() instanceof HTTPConduit)) {
//			HTTPConduit conduit = (HTTPConduit) client.getConduit();
//			HTTPClientPolicy policy = new HTTPClientPolicy();
//			policy.setConnectionTimeout(1500);
//			policy.setReceiveTimeout(60000);
//			conduit.setClient(policy);
//		}
		return (T) wsBean;
	}

	public Client createNoneJaxwsClient(String url, Class<?> intf) {
		ClientFactoryBean factoryBean = new ClientFactoryBean(new CXFPlusServiceBean());
		factoryBean.setAddress(url);
		factoryBean.setServiceClass(intf);
		if(trace){
			factoryBean.getInInterceptors().add(new LoggingInInterceptor());
			factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		return factoryBean.create();
	}

	@SuppressWarnings("unchecked")
	public <T> T createNoneJaxwsClientProxy(String url, Class<T> intf) {
		ClientFactoryBean cfb = new ClientFactoryBean(new CXFPlusServiceBean());
		ClientProxyFactoryBean factoryBean=new ClientProxyFactoryBean(cfb);
		factoryBean.setAddress(url);
		factoryBean.setServiceClass(intf);
		if(trace){
			factoryBean.getInInterceptors().add(new LoggingInInterceptor());
			factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		return (T)factoryBean.create();
	}
}
