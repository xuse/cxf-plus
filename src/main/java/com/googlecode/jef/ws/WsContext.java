package com.googlecode.jef.ws;

/**
 * WebService的发布描述信息，包括一个服务接口和一个服务实例
 * 可以用
 * <ul> {@link #getServiceBean()}</ul>
 *  <ul>{@link #serviceClass}</ul>获得需要的参数
 * @author jiyi
 *
 */
public class WsContext {
	private Class<?> serviceClass;
	private Object serviceBean;

	public WsContext(Object proxyBean, Class<?> wsIntf) {
		this.serviceBean = proxyBean;
		this.serviceClass = wsIntf;
	}

	public Class<?> getServiceClass() {
		return this.serviceClass;
	}

	public Object getServiceBean() {
		return this.serviceBean;
	}
}
