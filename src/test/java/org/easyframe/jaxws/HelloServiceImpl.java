package org.easyframe.jaxws;

public class HelloServiceImpl implements HelloService {
	public String sayHello(String name) {
		return "Hello," + name;
	}
}
