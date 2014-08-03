package org.easyframe.cxfplus.spring;

import org.easyframe.cxfplus.client.ClientFactory;
import org.easyframe.cxfplus.client.WsClientFactoryImpl;



public class JaxWsProxyBeanFactory extends AbstractProxyBeanFactory{
	@Override
	protected ClientFactory getFactory() {
		return WsClientFactoryImpl.getDefault();
	}
}
