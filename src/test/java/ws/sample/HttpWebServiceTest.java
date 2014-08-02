package ws.sample;

import java.util.Arrays;
import java.util.Map;

import jef.common.log.LogUtil;

import org.easyframe.cxfplus.client.Clients;
import org.junit.Test;

public class HttpWebServiceTest {
	@Test
	public void test(){
		String url="http://localhost/wstest/ws/serviceA";
		MyWsTest bean=Clients.createProxy(url, MyWsTest.class);
		System.out.println(new String(bean.getBytes()));
		System.out.println(bean.getFile());
		
		System.out.println(bean.getDate());
		System.out.println(bean.getTime());
		System.out.println(bean.getTimestamp());
		
		System.out.println(bean.getAInt());
		System.out.println(Arrays.toString(bean.getInts()));
		System.out.println(bean.getClz());
		System.out.println(bean.getClasses());
		System.out.println(bean.getState());
		
		System.out.println(bean.getPage());
		System.out.println(bean.getComplex());
		System.out.println(bean.getMapList());
		
	}
	
	@Test
	public void test2(){
		String url="http://localhost/wstest/ws/serviceA";
		MyWsTest bean=Clients.createProxy(url, MyWsTest.class);
		Map<String,Object> objmap=bean.getAttribute();
		LogUtil.show(objmap);
	}
}
