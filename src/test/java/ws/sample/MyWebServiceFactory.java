package ws.sample;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.CXFPlusClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.service.factory.CXFPlusServiceBean;
import org.easyframe.cxfplus.support.ServiceDefinition;
import org.easyframe.cxfplus.support.ServiceProcessor;

public class MyWebServiceFactory implements ServiceProcessor{
	
	/**
	 * 实现这个方法后，可以体检要发布的WebService对象和接口
	 * @param serviceBean  服务的实例对象  
	 * @param sei          服务的接口类
	 * @return  实际将要发布的Web服务接口类和对象实例。这两个值包装在一个{@link ServiceDefinition} 对象中。
	 */
	public ServiceDefinition processServiceDef(ServiceDefinition def) {
		try {
			Class<?> intf = Class.forName("WS_"+def.getServiceClass().getName());  //将WebService服务的发布接口换成另一个类
			return new ServiceDefinition(def.getName(),intf,def.getServiceBean());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}


	/**
	 * 如果你将服务端的接口类替换为其他类型了，那么也要负责生成一个客户端的代理对象。(使用JAX-WS)
	 * @see org.apache.cxf.jaxws.JaxWsProxyFactoryBean
	 * @param url
	 * @param intf
	 * @return
	 */
	public <T> T createClientProxy(String url, Class<T> intf) {
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean(new CXFPlusClientFactoryBean());
		factoryBean.setAddress(url);
		try {
			Class intf1 = Class.forName("WS_"+intf.getName());
			factoryBean.setServiceClass(intf1);
			Object wsBean = factoryBean.create();
			return (T) wsBean;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * 如果你将服务端的接口类替换为其他类型了，那么也要负责生成一个客户端的代理对象。(非JAX-WS)
	 * @see org.apache.cxf.frontend.ClientFactoryBean
	 * @param url
	 * @param intf
	 * @return
	 */
	public Client createNoneJaxwsClient(String url, Class<?> intf) {
		ClientFactoryBean factoryBean = new ClientFactoryBean(new CXFPlusServiceBean());
		factoryBean.setAddress(url);
		try {
			Class intf1 = Class.forName("WS_"+intf.getName());
			factoryBean.setServiceClass(intf1);
			return factoryBean.create();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}


	public <T> T createNoneJaxwsClientProxy(String url, Class<T> intf) {
		ClientFactoryBean cf1 = new ClientFactoryBean(new CXFPlusServiceBean());
		ClientProxyFactoryBean factoryBean=new ClientProxyFactoryBean(cf1);
		
		factoryBean.setAddress(url);
		try {
			Class intf1 = Class.forName("WS_"+intf.getName());
			factoryBean.setServiceClass(intf1);
			return (T)factoryBean.create();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
