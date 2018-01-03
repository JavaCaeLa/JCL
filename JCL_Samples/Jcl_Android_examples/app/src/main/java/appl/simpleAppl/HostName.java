package appl.simpleAppl;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostName {
	
	public String exec(){
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
