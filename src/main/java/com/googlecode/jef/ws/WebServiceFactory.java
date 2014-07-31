package com.googlecode.jef.ws;

import org.apache.cxf.endpoint.Client;

/**
 * 这个接口可以定制WebService类的发布行为。
 * 要定制发布行为，你可以实现这个接口
 * 
 * @author jiyi
 *
 */
public interface WebServiceFactory {
	
	/**
	 * 实现这个方法后，可以体检要发布的WebService对象和接口
	 * @param serviceBean  服务的实例对象  
	 * @param sei          服务的接口类
	 * @return  实际将要发布的Web服务接口类和对象实例。这两个值包装在一个{@link WsContext} 对象中。
	 */
	WsContext createServerBean(Object serviceBean, Class<?> sei);
	
	/**
	 * 如果你将服务端的接口类替换为其他类型了，那么也要负责生成一个客户端的代理对象。(使用JAX-WS)
	 * @see org.apache.cxf.jaxws.JaxWsProxyFactoryBean
	 * @param url
	 * @param intf
	 * @return
	 */
	<T> T createClientProxy(String url, Class<T> intf);
	
	/**
	 * 生成WebServiceClient
	 * @param url
	 * @param intf
	 * @return
	 */
	Client createNoneJaxwsClient(String url, Class<?> intf);
	
	/**
	 * 如果你将服务端的接口类替换为其他类型了，那么也要负责生成一个客户端的代理对象。
	 * @see org.apache.cxf.frontend.ClientFactoryBean
	 * @param url
	 * @param intf
	 * @return
	 */
	<T> T createNoneJaxwsClientProxy(String url, Class<T> intf);
	
	
}
