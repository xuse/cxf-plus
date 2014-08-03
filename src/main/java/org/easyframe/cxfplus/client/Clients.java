package org.easyframe.cxfplus.client;


public class Clients {
	public static <T> T createProxy(String url, Class<T> clz) {
		return WsClientFactoryImpl.getDefault().createProxy(url, clz);
	}

}
