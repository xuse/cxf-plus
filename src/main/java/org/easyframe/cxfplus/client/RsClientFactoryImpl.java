package org.easyframe.cxfplus.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.easyframe.jaxrs.FastJSONProvider;

public class RsClientFactoryImpl implements ClientFactory{

	private boolean trace;
	
	@SuppressWarnings("unchecked")
	public <T> T createProxy(String url, Class<T> clz) {
		JAXRSClientFactoryBean proxyFactoryBean = new JAXRSClientFactoryBean();
		proxyFactoryBean.setAddress(url);
		proxyFactoryBean.setServiceClass(clz);
		proxyFactoryBean.setProvider(new FastJSONProvider(true,false));
		if (trace) {
			proxyFactoryBean.getInInterceptors().add(new LoggingInInterceptor());
			proxyFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		T client = (T) proxyFactoryBean.create();
		return client;
	}

	public Client createClient(String url, Class<?> clz) {
		throw new UnsupportedOperationException();
	}

	public RsClientFactoryImpl setTrace(boolean trace) {
		this.trace=trace;
		return this;
	}
}
