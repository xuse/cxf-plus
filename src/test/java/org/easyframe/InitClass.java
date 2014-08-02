package org.easyframe;

import org.easyframe.cxfplus.CXFPlusServlet;
import org.easyframe.cxfplus.support.SimpleServiceLookup;
import org.easyframe.jaxrs.PeopleService;
import org.easyframe.jaxrs.PeopleServiceImpl;
import org.easyframe.jaxrs.PeopleServiceXml;
import org.easyframe.jaxws.HelloService;
import org.easyframe.jaxws.HelloServiceImpl;

public class InitClass {
	public InitClass(CXFPlusServlet servlet){
		SimpleServiceLookup data=new SimpleServiceLookup();
		PeopleServiceImpl p=new PeopleServiceImpl();
		data.addService(PeopleServiceXml.class, p);
		data.addService(HelloService.class, new HelloServiceImpl());
		data.addService(PeopleService.class, p);
		servlet.setRSlookup(data);
		servlet.setWSlookup(data);
	}

}
