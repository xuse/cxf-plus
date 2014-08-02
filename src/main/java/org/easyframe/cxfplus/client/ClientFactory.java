package org.easyframe.cxfplus.client;

import org.apache.cxf.endpoint.Client;

public interface ClientFactory {
	
	<T> T createProxy(String url, Class<T> clz);
	
	Client createClient(String url,Class<?> clz);

	ClientFactory setTrace(boolean trace);
}
