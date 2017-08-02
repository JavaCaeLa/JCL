package implementations.dm_kernel.host;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.Server;
import implementations.dm_kernel.IoTuser.Board;
import implementations.sm_kernel.JCL_FacadeImpl;
import implementations.sm_kernel.JCL_orbImpl;
import implementations.sm_kernel.PacuResource;
import implementations.util.CoresAutodetect;
import implementations.util.DirCreation;
import implementations.util.ServerDiscovery;
import implementations.util.IoT.CryptographyUtils;
import implementations.util.IoT.JCL_IoT_SensingModelRetriever;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_get_host;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import mraa.mraa;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;

public class MainHost extends Server{
	private String hostPort;
	private static String nic;
//	private String[] hostIp = new String[5];
	private Map<String,String> metaData;
	static boolean twoStep = false;
	private HashSet<String> TaskContain;
	private static TrayIconJCL icon;
	private Map<Long, JCL_result> results;
	private ConcurrentHashMap<String, Set<Object>> JclHashMap;
	private GenericResource<JCL_task> rp;
	private ConcurrentHashMap<Long,String> JCLTaskMap;
	private AtomicInteger registerMsg;
	private ConcurrentMap<String,String[]> slaves;
	private List<String> slavesIDs;
	private AtomicLong taskID;
	private String serverAdd;
	private int serverPort;
	private static int BoardType;
	private JCL_FacadeImpl jcl;

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int hostPort = Integer.parseInt(properties.getProperty("hostPort"));
		nic = properties.getProperty("nic");
		twoStep = Boolean.parseBoolean(properties.getProperty("twoStep").trim());
//		int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
		String BoardID = properties.getProperty("deviceID");
		BoardType = Integer.parseInt( properties.getProperty("deviceType"));
		ConnectorImpl.encryption = Boolean.parseBoolean(properties.getProperty("encryption"));	

		DirCreation.createDirs("../jcl_temp/");
			
		
		if (BoardType >= 4){	// creates a thread to start sensing
			Thread t = new Thread(new Board());
			t.start();
		}
		
		try {
			
			new MainHost(hostPort,BoardID);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MainHost(int port, String BoardID) throws IOException{
		super(port);
		this.hostPort = Integer.toString(port);
		this.metaData = getNameIPPort();
		if ( BoardType >= 4){
			try{
				this.metaData.put("DEVICE_PLATFORM", mraa.getPlatformName());	
			}catch(Exception e){
				this.metaData.put("DEVICE_PLATFORM", "Generic Host");
			}
			
		}else{
			this.metaData.put("DEVICE_PLATFORM", "Generic Host");
		}
		this.metaData.put("DEVICE_TYPE",String.valueOf(BoardType));
		this.metaData.put("DEVICE_ID",BoardID);
		this.slavesIDs = new LinkedList<String>();
		this.slaves = new ConcurrentHashMap<String, String[]>();
		this.rp = new PacuResource<JCL_task>(this.slavesIDs, this.slaves, twoStep);
		this.JCLTaskMap = new ConcurrentHashMap<Long,String>();
		this.TaskContain = new HashSet<String>();
		this.results =  new ConcurrentHashMap<Long, JCL_result>();
		this.JclHashMap = new ConcurrentHashMap<String, Set<Object>>();
		this.taskID = new AtomicLong();
		this.jcl = (JCL_FacadeImpl)JCL_FacadeImpl.Holder.getInstancePacu(rp);
    this.registerMsg = new AtomicInteger();
		JCL_handler.setRegisterMsg(registerMsg);
		JCL_orbImpl.setRegisterMsg(registerMsg);
    
    try{
		icon = new TrayIconJCL(this.metaData);
		}catch(ExceptionInInitializerError e){
			System.out.println("Unable to load tray icon");
		}
		this.begin();
	}

	@Override
	protected void beforeListening() {
		
		
		
		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		serverAdd = properties.getProperty("serverMainAdd");
		serverPort = Integer.parseInt(properties.getProperty("superPeerMainPort"));

		Thread threadRegister = new Thread(){
		    public void run(){
		    	JCL_connector controlConnector = new ConnectorImpl(false);
		    	if(!controlConnector.connect(serverAdd, serverPort,null)){
		    		serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
		    		boolean connected = controlConnector.connect(serverAdd, serverPort,null);
		    		if (!connected){
		    			String serverData[] = ServerDiscovery.discoverServer();
		    			if (serverData != null){
		    				serverAdd = serverData[0];
		    				serverPort = Integer.parseInt(serverData[1]);
		    				controlConnector.connect(serverAdd, serverPort, null);
		    			}
		    			
		    		}
		    	}
		    	JCL_message_metadata msg = new MessageMetadataImpl();

		    	msg.setType(-1);				
				msg.setMetadados(metaData);
				
				boolean activateEncryption = false;
				if (ConnectorImpl.encryption){
					ConnectorImpl.encryption = false;
					activateEncryption = true;
				}
				
				JCL_message_get_host msgr = (JCL_message_get_host)controlConnector.sendReceiveG(msg,null);				
				
				if (activateEncryption)
					ConnectorImpl.encryption = true;
											
				if((msgr.getSlaves() != null)){	
					slaves.putAll(msgr.getSlaves());
					slavesIDs.addAll(msgr.getSlavesIDs());
					CryptographyUtils.setClusterPassword(msgr.getMAC());
					
					((PacuResource)rp).setHostIp(metaData);
					rp.wakeup();
					
					if (BoardType >= 4)
						configureBoard();
					
					System.out.println("HOST JCL is OK");					 			
				}				
				else System.err.println("HOST JCL NOT STARTED");
				
				ShutDownHook();
				controlConnector.disconnect();
		    }
		  };
		  threadRegister.start();
	}
	
//	private String[] getNameIPPort(){
	private Map<String,String> getNameIPPort(){
		Map<String,String> IPPort = new HashMap<String,String>();
		try {			
			//InetAddress ip = InetAddress.getLocalHost();
			InetAddress ip = getLocalHostLANAddress();
			System.out.println("Current IP address : " + ip.getHostAddress());
	 
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	 
			byte[] mac = network.getHardwareAddress();
	 
			System.out.print("Current MAC address : ");
	 
			StringBuilder sb = new StringBuilder(17);
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			
			if (sb.length()==0) sb.append(ip.getHostAddress());
			
			System.out.println(sb.toString());
		//	String[] result = {ip.getHostAddress(), hostPort, sb.toString(),Integer.toString(CoresAutodetect.cores)};
			
			IPPort.put("IP", ip.getHostAddress());
			IPPort.put("PORT", hostPort);
			IPPort.put("MAC", sb.toString());
			IPPort.put("CORE(S)", Integer.toString(CoresAutodetect.cores));

			return IPPort;
			
	 
		} catch (Exception e) {
			
			try {
				InetAddress ip = InetAddress.getLocalHost();			
				String sb = ip.getHostAddress();
				
				byte[] mac = macConvert(sb);
				StringBuilder sbS= new StringBuilder(17);
				for (int i = 0; i < mac.length; i++) {
					sbS.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				
			//	String[] result = {sb, this.hostPort, sb, Integer.toString(CoresAutodetect.cores)};
				IPPort.put("IP", sb);
				IPPort.put("PORT", hostPort);
				IPPort.put("MAC", sbS.toString());
				IPPort.put("CORE(S)", Integer.toString(CoresAutodetect.cores));

				return IPPort;
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

	@Override
	protected void duringListening() {
		// TODO Auto-generated method stub		
	}

	@Override
	public <K extends JCL_handler> GenericConsumer<K> createSocketConsumer(
			GenericResource<K> r, AtomicBoolean kill){
		// TODO Auto-generated method stub		
		
		String hostID = this.metaData.get("MAC")+this.metaData.get("PORT"); 		
		return new SocketConsumer<K>(r,kill,TaskContain,hostID,results,this.taskID,this.JclHashMap,this.rp,this.JCLTaskMap,this.jcl);
	}
	
	private void ShutDownHook() {
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	    	
	      @Override
	      public void run() {
	    	try {
	    		
	    		
		    JCL_message_metadata msg = new MessageMetadataImpl();
		    msg.setType(-2);				
			msg.setMetadados(metaData);
	    		
	    		
//	        JCL_message_control msg = new MessageControlImpl();
//			msg.setType(-2);
//		//	String[] hostIpN = Arrays.copyOf(hostIp, hostIp.length + 1);
//		//	hostIpN[hostIp.length] = 
//			msg.setRegisterData(hostIp);			

			// Read properties file.
			Properties properties = new Properties();
			try {
			    properties.load(new FileInputStream("../jcl_conf/config.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
//			String serverAdd = properties.getProperty("serverMainAdd");
//			int serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
//			final int superPeerPort = Integer.parseInt(properties.getProperty("superPeerMainPort"));
//			boolean verbose = Boolean.parseBoolean(properties.getProperty("verbose"));
			JCL_connector controlConnector = new ConnectorImpl(false);
			if(controlConnector.connect(serverAdd, serverPort,null)){			
				JCL_message_control msgr = (JCL_message_control) controlConnector.sendReceiveG(msg,null);
				if(msgr.getRegisterData().length==1){	
					System.out.println("HOST JCL WAS UNREGISTERED!");
				}
				else System.err.println("HOST JCL WAS NOT UNREGISTERED!");					
				controlConnector.disconnect();			
	    		}			
			ConnectorImpl.closeSocketMap();
	    	} 	    	
	    	catch (Exception e) {
				System.err.println("Erro in unregister host!");
			}
	      }
	    });
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
	
	protected void configureBoard(){
		try{
			System.loadLibrary("mraajava");
			Board.setBoardIP(this.metaData.get("IP"));
			Board.setPort(this.metaData.get("PORT"));
			Board.setMac(this.metaData.get("MAC"));
			Board.setCore(this.metaData.get("CORE(S)"));		
			Board.setDeviceType(this.metaData.get("DEVICE_TYPE"));
			Board.setDeviceAlias(this.metaData.get("DEVICE_ID"));
			Board.setServerIP(this.serverAdd);
			Board.setServerPort(String.valueOf(this.serverPort));
			Board.setStandBy(false);
			System.out.println("mraa: " + mraa.getPlatformName());
			Board.setSensingModel(JCL_IoT_SensingModelRetriever.getSensingModel(mraa.getPlatformName()));
			Board.setPlatform(mraa.getPlatformName());
			Board.restore();
			Properties properties = new Properties();
			try {
			    properties.load(new FileInputStream("../jcl_conf/config.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (properties.getProperty("allowUser") != null)
				Board.setAllowUser(Boolean.valueOf(properties.getProperty("allowUser")));
			
			if (properties.getProperty("mqttBrokerAdd")!=null && properties.getProperty("mqttBrokerPort") != null){
				Board.setBrokerIP(properties.getProperty("mqttBrokerAdd"));
				Board.setBrokerPort(properties.getProperty("mqttBrokerPort"));
				Board.connectToBroker();
			}
		}catch(Exception e){
			System.err.println("Can't config Host to sensing!!!");
		}
	}
}
