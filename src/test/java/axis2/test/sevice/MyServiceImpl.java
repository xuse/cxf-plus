package axis2.test.sevice;

public class MyServiceImpl implements MyService{

	public String toBaseString(String input, int input2) {
		System.out.println(input+"  "+input2);
		return input+input2;
	}

	public String getHello(String name) {
		return "Hello, "+name+"!";
	}

	public void update(String data) {
		System.out.println(data);
	}

}