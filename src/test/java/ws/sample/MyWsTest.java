package ws.sample;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import jef.common.wrapper.Page;

import org.easyframe.cxfplus.support.IWebService;

@WebService()
public interface MyWsTest extends IWebService {
	Map<String, String> method1();
	
	Map<String, String>[] getMapList();

	Map<String, Double> method2(String[] paramArrayOfString);

	Map<String, String> method3(Map<String, Long> paramMap,String arg2);
	

	File getFile();
	
	byte[] getBytes();
	
	java.sql.Time getTime();
	
	java.sql.Date getDate();
	
	java.sql.Timestamp getTimestamp();
	
	int[] getInts();
	
	AtomicInteger getAInt();
	
	Class getClz();
	
	Set<Class> getClasses();
	
	Thread.State getState();
	
	Page<String> getPage();
	
	Page<Map<String,String>> getComplex();
	
	Map<String,Object> getAttribute();
}