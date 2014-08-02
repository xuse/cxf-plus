package org.easyframe.cxfplus.support;

/**
 * WebService的发布描述信息，包括一个服务接口和一个服务实例
 * 可以用
 * <ul> {@link #getServiceBean()}</ul>
 *  <ul>{@link #serviceClass}</ul>获得需要的参数
 * @author jiyi
 *
 */
public class ServiceDefinition {
	private String name;
	private Class<?> serviceClass;
	private Object serviceBean;
	private String path;
	
	public String getName() {
		if(name==null)return serviceClass.getName();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceDefinition(String name,Class<?> sei,Object serviceInstance) {
		this.name=name;
		this.serviceClass = sei;
		this.serviceBean = serviceInstance;
	}

	public Class<?> getServiceClass() {
		return this.serviceClass;
	}

	public Object getServiceBean() {
		return this.serviceBean;
	}

	public String getPath() {
		if(path==null){
			 return "/" + getName();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
