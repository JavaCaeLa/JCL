package appl.simpleAppl;

import java.io.File;
import java.util.Map;

import implementations.collections.JCLHashMap;
import implementations.collections.JCLHashMapPacu;
import implementations.dm_kernel.user.JCL_FacadeImpl;

public class appl7 {

	
	// Just Pacu version JCLHashMap
	public static void main(String[] args) {
		appl7 app = new appl7();
	}
	
	public appl7() {
		
		// TODO Auto-generated constructor stub
		Map<String, String> m = JCL_FacadeImpl.GetHashMap("mymap");
		Map<String, String> m1 = new JCLHashMap<String, String>("mymap1");
		
		m.put("Key1", "value1");
		m.put("Key2", "value2");
		m.put("Key3", "value3");
		m.put("Key4", "value4");
		m.put("Key5", "value5");
		m.put("Key6", "value6");
		m.put("Key7", "value7");
		m.put("Key8", "value8");

		Map<String, String> m2 = new JCLHashMap<String, String>("mymap");
		
		for(int i=1;i<9;i++){
			String key = "Key"+i;
			System.out.println("Key"+i+" value: "+m2.get(key));
		}
		
		File[] book = {new File("../user_jars/book.jar")};
		Map<String, Book> b = new JCLHashMap<String, Book>("usertypemap");
		Map<String, Book> b1 = JCL_FacadeImpl.GetHashMap("usertypemap1");
		Book myBook = new Book("author", "editor", 1024, 2015);
		b.put("1", myBook);
		b1.put("1", myBook);
		myBook = b.get("1");
		//any other Java map interface method can be invoked!!
		myBook.print();
		JCLHashMapPacu.destroy();
		
	}
}
