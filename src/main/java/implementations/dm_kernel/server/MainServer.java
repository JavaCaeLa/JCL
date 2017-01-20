package implementations.dm_kernel.server;


import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.Server;
import implementations.dm_kernel.router.Router;
import implementations.util.IoT.CryptographyUtils;
import interfaces.kernel.JCL_message_register;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;

public class MainServer extends Server{
	
//	private ConcurrentMap<String,String[]> slaves,jarsName;
	private ConcurrentMap<Integer,ConcurrentMap<String,String[]>> slaves_IoT;
	private List<Entry<String, Map<String, String>>> devicesExec;
//	private ConcurrentMap<String,String[]> jarsName_IoT;
	private ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata_IoT;
	private ConcurrentMap<Object,String[]> globalVarSlaves;
	private ConcurrentMap<String,List<String>> jarsSlaves;
	private ConcurrentMap<Integer,List<String>> slavesIDs_IoT;

	//	private static ConcurrentMap<String,SocketChannel> connect;
	private ConcurrentMap<String,JCL_message_register> jars;
	private ConcurrentMap<String,String[]> runningUser;	
//	private List<String> slavesIDs;
	private static Boolean verbose;
	private static String nic;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		connect = new ConcurrentHashMap<String,SocketChannel>();
		

		
		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
		int routerPort = Integer.parseInt(properties.getProperty("routerMainPort"));
//		int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
		ConnectorImpl.encryption = Boolean.parseBoolean(properties.getProperty("encryption"));
		
//		JCL_handler.buffersize = byteBuffer;
//		ConnectorImpl.buffersize = byteBuffer;
//		commom.JCL_connector.buffersize = byteBuffer;
		
		nic = properties.getProperty("nic");

//		int timeOut = Integer.parseInt(properties.getProperty("timeOut"));		
		verbose =  Boolean.parseBoolean(properties.getProperty("verbose"));
//		ConnectorImpl.setSocketConst(connect,timeOut);
//		ConnectorImpl.setSocketConst(timeOut);
		
		try {
			new MainServer(serverPort,routerPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MainServer(int portS, int portR) throws IOException{
		
		//Start Server
		super(portS);
		CryptographyUtils.setClusterPassword(this.getMac());
		this.globalVarSlaves = new ConcurrentHashMap<Object, String[]>();
//		this.slavesIDs = new LinkedList<String>();
		this.slavesIDs_IoT = new ConcurrentHashMap<Integer,List<String>>();
		this.slaves_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();
//		this.jarsName_IoT = new ConcurrentHashMap<String,String[]>();
		this.metadata_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,Map<String,String>>>();		
//		this.slaves = new ConcurrentHashMap<String, String[]>();
		this.jarsSlaves = new ConcurrentHashMap<String,List<String>>();
//		this.jarsName = new ConcurrentHashMap<String, String[]>();
		this.jars = new ConcurrentHashMap<String, JCL_message_register>();
		this.runningUser = new ConcurrentHashMap<String, String[]>();
		this.devicesExec = new ArrayList<Entry<String, Map<String, String>>>();
//		RoundRobin.ini(this.devicesExec);

		System.err.println("JCL server ok!");
		
		//Router Super-Peer 		
		new Thread(new Router(portR,super.getServerR(),nic)).start();
		System.err.println("JCL router ok!");
		
		this.begin();

				
	}

	@Override
	protected void beforeListening() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void duringListening() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K extends JCL_handler> GenericConsumer<K> createSocketConsumer(
			GenericResource<K> r, AtomicBoolean kill){
		// TODO Auto-generated method stub
		return new SocketConsumer<K>(r,kill, this.globalVarSlaves, this.slavesIDs_IoT, this.slaves_IoT,this.jarsSlaves,this.jars,verbose,runningUser,metadata_IoT,this.devicesExec);

	}
	
	private String getMac(){
//		Map<String,String> IPPort = new HashMap<String,String>();
		try {			
			//InetAddress ip = InetAddress.getLocalHost();
			InetAddress ip = getLocalHostLANAddress();
//			System.out.println("Current IP address : " + ip.getHostAddress());
	 
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	 
			byte[] mac = network.getHardwareAddress();
	 
//			System.out.print("Current MAC address : ");
	 
			StringBuilder sb = new StringBuilder(17);
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			
			if (sb.length()==0) sb.append(ip.getHostAddress());
			
			System.out.println(sb.toString());

			return sb.toString();
			
	 
		} catch (Exception e) {
			
			try {
				InetAddress ip = InetAddress.getLocalHost();			
				String sb = ip.getHostAddress();
				
				byte[] mac = macConvert(sb);
				StringBuilder sbS= new StringBuilder(17);
				for (int i = 0; i < mac.length; i++) {
					sbS.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				
				return sbS.toString();
			} catch (UnknownHostException e1) {
				System.err.println("cannot collect host address");
				return null;
			}
			
			
		}
	}
	
	public byte[] macConvert(String macAddress){
		
		String[] macAddressParts = macAddress.split("-");
		byte[] macAddressBytes = new byte[6];

		if (macAddressParts.length == 6){
		// convert hex string to byte values
			for(int i=0; i<6; i++){
				Integer hex = Integer.parseInt(macAddressParts[i], 16);
				macAddressBytes[i] = hex.byteValue();
			}
		
		}else{
			String[] ipAddressParts = macAddress.split("\\.");
			for(int i=0; i<4; i++){
			    Integer integer = Integer.parseInt(ipAddressParts[i]);
			    macAddressBytes[i] = integer.byteValue();
			}
			Integer integer = 0;
			macAddressBytes[4] =  integer.byteValue();
			macAddressBytes[5] =  integer.byteValue();
		}		
			return macAddressBytes;
	}
	
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
	    try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();){
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            
	            if (iface.getName().contains(nic)){
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                        return inetAddr;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	          }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	            return candidateAddress;
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        return jdkSuppliedAddress;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}
}
