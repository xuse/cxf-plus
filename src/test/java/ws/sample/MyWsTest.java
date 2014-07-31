package ws.sample;

import java.util.Map;

import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import jef.common.SimpleMapAdapter;

import com.googlecode.jef.ws.IWebService;

@WebService
public abstract interface MyWsTest extends IWebService {
	public abstract Map<String, String> method1();

	public abstract Map<String, String> method2(String[] paramArrayOfString);

	public abstract Map<String, String> method3(@XmlJavaTypeAdapter(SimpleMapAdapter.class) Map<String, Long> paramMap);
}
