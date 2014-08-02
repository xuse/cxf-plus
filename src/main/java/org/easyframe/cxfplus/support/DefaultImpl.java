package org.easyframe.cxfplus.support;

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
import org.easyframe.jaxws.interceptors.TraceHandler;

/**
 * 简单的WsFactory实现。
 * @author jiyi
 *
 */
public class DefaultImpl implements ServiceProcessor {
	private static DefaultImpl instance = new DefaultImpl();
	private final Map<String, ServiceDefinition> data = new HashMap<String, ServiceDefinition>();
	private boolean trace;

	public static DefaultImpl getInstance() {
		return instance;
	}
	
	public static DefaultImpl getInstance(boolean trace) {
		instance.trace=trace;
		return instance;
	}

	public ServiceDefinition processServiceDef(ServiceDefinition def) {
		return def;
	}

	public Map<String, ServiceDefinition> getRegistedService() {
		return Collections.unmodifiableMap(this.data);
	}
	
	public void register(String name, IWebService service, Class<?> serviceClass) {
		this.data.put(name, new ServiceDefinition(name,serviceClass,service));
	}

	public void init() {
	}
}
