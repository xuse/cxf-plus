package org.easyframe.cxfplus.client;


public class Clients {
	private static WsClientFactoryImpl jaxwsPlus=new WsClientFactoryImpl().setTrace(true);

	public static <T> T createProxy(String url, Class<T> clz) {
		return jaxwsPlus.createProxy(url, clz);
	}

}
