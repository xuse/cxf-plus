package org.easyframe.cxfplus.spring;

import org.easyframe.cxfplus.client.ClientFactory;
import org.easyframe.cxfplus.client.RsClientFactoryImpl;



public class JaxRsProxyBeanFactory extends AbstractProxyBeanFactory{
	@Override
	protected ClientFactory getFactory() {
		return RsClientFactoryImpl.getDefault();
	}
}
