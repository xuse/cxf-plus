package axis2.test.sevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Test;

/**
 * 这是孙喜欢的调用方式，将远程WS当做反射类一样去调用。
 * @author jiyi
 *
 */
public class DynamicCall {
	private static JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
	private static Map<String, Client> cxfClients = new ConcurrentHashMap<String, Client>();

	/**
	 * 从缓存中取用Client
	 * @param url
	 * @return
	 */
	private static Client getCxfClient(String url) {
		Client client = cxfClients.get(url);
		if (client == null) {
			client = dcf.createClient(url);
			cxfClients.put(url, client);
			client.getConduit().getTarget().getAddress().setValue(url);
		}
		return client;
	}

	/**
	 * 调用远程WS的服务
	 * 
	 * 这种调用方法的优点是——
	 * 1、无需生成类
	 * 2、无需对方的接口
	 * 3、容错性强，两边接口无需强行维持一致。
	 * 
	 * 缺点是——校验薄弱，schema和数据类型不一致等错误可能发生，需要调用方人为确保调用参数类型的正确性。
	 * 
	 * 
	 * @param url    服务地址
	 * @param method 调用方法
	 * @param namespaceURI 服务命名空间
	 * @param args     参数
	 * @param returnType 返回类型
	 * @return 远程调用后的结果
	 * @throws Exception
	 */
	public static <T> T callWsCxf(String url, String method, String namespaceURI, Object[] args, Class<T> returnType) throws Exception {
		Client client = getCxfClient(url);
		QName name = new QName(namespaceURI, method);
		Object[] obj = client.invoke(name, args);
		return (T) obj[0];
	}

	@Test
	public void testCxf() throws Exception {
		String str = callWsCxf("http://10.17.48.87:8089/apollo-web/services/remoteLicenseService?wsdl", "getLicenseXml", "http://service.license.module.publicservice.apollo.vision/", new Object[] {}, String.class);
		System.out.println(str);
	}
	
	/**
	 * 这是孙希望使用纯动态调用时，前来咨询修改URL的API时做的测试。
	 * @throws Exception
	 */
	@Test
	public void testcxf() throws Exception{
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();  
		org.apache.cxf.endpoint.Client client = dcf.createClient("http://pc-sunyingjie/cxf/services/cxfTestService?wsdl");
		
		//////////////// KEY!!!!!!!!!!!!!!!!! ////////////////
		client.getConduit().getTarget().getAddress().setValue("http://pc-sunyingjie/cxf/services/cxfTestService");
		//////////////KEY!!!!!!!!!!!!!!!!! ////////////////
		
		QName name=new QName("http://foo/","hello");  
		List<String> emails=new ArrayList<String>();
		emails.add("sss@sina.com");
		emails.add("ddd@163.com");
		Object[] objects=client.invoke(name,new Object[]{"sun",15,emails,15l});   
		System.out.println(objects[0].toString());
	}

}
