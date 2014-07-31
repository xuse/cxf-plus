package com.googlecode.jef.ws;

import java.lang.reflect.Method;
import jef.common.wrapper.Holder;
import org.springframework.context.ApplicationContextAware;

/**
 * 拦截器接口，用于拦截WS在服务端的处理过程。
 * 
 * 当初wuyj提出的构想，WebService的实现类是一个自行定义行为的Mock。
 * @author jiyi
 *
 */
public interface WsCallHandler extends ApplicationContextAware {
	/**
	 * 提供开发者自行定义的Handler来处理Web调用
	 * 
	 * @param bean
	 *            Service Bean
	 * @param method
	 *            被调用的方法
	 * @param args
	 *            被调用的参数
	 * @param result
	 *            可以设置返回结果(必须return true才生效)
	 * @return 当返回true时，(拦截器模式)——认为拦截生效，框架不会去调用原本的方法
	 *         当返回false时：(过滤器模式)——还是会去调用原本的方法，返回原本的结果
	 * @throws Throwable
	 */
	boolean invoke(Object bean, Method method, Object[] args, Holder<Object> result) throws Throwable;
}
