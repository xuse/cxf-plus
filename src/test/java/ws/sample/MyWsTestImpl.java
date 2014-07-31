package ws.sample;

import java.util.HashMap;
import java.util.Map;

public class MyWsTestImpl implements MyWsTest {
	private final HashMap<String, String> rr;

	public MyWsTestImpl() {
		this.rr = new HashMap<String,String>();
		this.rr.put("123", "aaa");
		this.rr.put("456", "bbb");
	}

	public Map<String, String> method1() {
		return this.rr;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> method2(String[] str2) {
		@SuppressWarnings("rawtypes")
		Map map=(Map) rr.clone();
		for(String s:str2){
			map.put(s, s);
		}
		return map;
	}

	public Map<String, String> method3(Map<String, Long> param) {
		return this.rr;
	}
}
