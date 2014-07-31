package ws.sample;

import java.util.List;
import java.util.Map;

@javax.jws.WebService
public interface CxfPlusFeature {
	Foo<Foo> getGenericObject();
	
	Map<String,String> getMap();
	
	Map<String,Integer> getVarMap();
	
	static class Foo<T> extends Page<Integer,T>{
		private String name;
		private List<T> list;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<T> getList() {
			return list;
		}
		public void setList(List<T> list) {
			this.list = list;
		}
	}
	static interface IFoo<T>{
		List<T> getList(); 
	}
	static abstract class Page<B,T> implements IFoo<T>{
		private B total;

		public B getTotal() {
			return total;
		}

		public void setTotal(B total) {
			this.total = total;
		}
	}
}
