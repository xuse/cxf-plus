package org.easyframe.cxfplus.support;

import org.apache.cxf.endpoint.Client;
import org.easyframe.cxfplus.support.ServiceDefinition;

/**
 * 这个接口可以定制WebService类的发布行为。
 * 要定制发布行为，你可以实现这个接口
 * 
 * @author jiyi
 *
 */
public interface ServiceProcessor {
	
	/**
	 * 实现这个方法后，可以体检要发布的WebService对象和接口
	 * @param def  服务定义
	 * @return  实际将要发布的Web服务接口类和对象实例。这两个值包装在一个{@link ServiceDefinition} 对象中。
	 */
	ServiceDefinition processServiceDef(ServiceDefinition def);
	
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
