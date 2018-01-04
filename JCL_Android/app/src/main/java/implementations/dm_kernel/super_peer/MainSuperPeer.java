package implementations.dm_kernel.super_peer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_connector;
import commom.JCL_handler;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.Server;
import implementations.util.CoresAutodetect;

import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_register;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;

public class MainSuperPeer extends Server{

	//	private ConcurrentMap<String,String[]> slaves,jarsName;
	private ConcurrentMap<Integer,ConcurrentMap<String,String[]>> slaves_IoT,jarsName_IoT,metadata_IoT;
	private ConcurrentMap<Object,String[]> globalVarSlaves;
	private ConcurrentMap<String,List<String>> jarsSlaves;
	private ConcurrentMap<Integer,List<String>> slavesIDs_IoT;
	//	private static ConcurrentMap<String,SocketChannel> connect;
	private AtomicLong numOfTasks;
	private ConcurrentMap<String,JCL_message_register> jars;
	private JCL_connector routerLink;
	Map<String,String> metaData;
//	private ConcurrentMap<String,String[]> runningUser;	
	private ConcurrentMap<Long,Object[]> taskLocation;
	//	private List<String> slavesIDs;
	private static Boolean verbose;
	private static String nic,serverAdd;
	private static int routerPort,routerLinks;
	
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int superPeerPort = Integer.parseInt(properties.getProperty("superPeerMainPort"));
		routerPort = Integer.parseInt(properties.getProperty("routerMainPort"));
		routerLinks = Integer.parseInt(properties.getProperty("routerLink"));		
		serverAdd = properties.getProperty("serverMainAdd");		
		verbose =  Boolean.parseBoolean(properties.getProperty("verbose"));
		nic = properties.getProperty("nic");
		int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
		
		//JCL_handler.buffersize = byteBuffer;
		//ConnectorImpl.buffersize = byteBuffer;
		//JCL_connector.buffersize = byteBuffer;
		
		
		try {
			new MainSuperPeer(superPeerPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public MainSuperPeer(int portS) throws IOException {		
		//Start Server
		super(portS);

		this.globalVarSlaves = new ConcurrentHashMap<Object, String[]>();
//		this.slavesIDs = new LinkedList<String>();
		this.slavesIDs_IoT = new ConcurrentHashMap<Integer,List<String>>();
		this.slaves_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();
		this.jarsName_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();
		this.metadata_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();		
//		this.slaves = new ConcurrentHashMap<String, String[]>();
		this.jarsSlaves = new ConcurrentHashMap<String,List<String>>();
//		this.jarsName = new ConcurrentHashMap<String, String[]>();
		this.jars = new ConcurrentHashMap<String, JCL_message_register>();
//		this.runningUser = new ConcurrentHashMap<String, String[]>();
		this.numOfTasks = new AtomicLong(0);
		this.taskLocation = new ConcurrentHashMap<Long, Object[]>();
        this.routerLink = new JCL_connector();
        this.metaData = getNameIPPort();

		
				
//		System.err.println("JCL server ok!");
				
		//Router Super-Peer 		
		//new Thread(new Router(portR,super.getServerR())).start();
		//System.err.println("JCL router ok!");
				
		this.begin();
	}

	@Override
	public <K extends JCL_handler> GenericConsumer<K> createSocketConsumer(GenericResource<K> r, AtomicBoolean kill) {
		// TODO Auto-generated method stub
		return new SocketConsumer<K>(r,kill, this.globalVarSlaves, this.slavesIDs_IoT, this.slaves_IoT,this.jarsSlaves,this.jarsName_IoT,this.jars,verbose,taskLocation,numOfTasks, this.routerLink,metaData);
	}

	@Override
	protected void beforeListening() {
		// TODO Auto-generated method stub
		try {
			
			
			 Set<SelectionKey> LK =  this.selector.keys();
	           
	           for(SelectionKey k:LK){
	        	  System.out.println("int OP:"+k.interestOps());
	           }
			
			
			
			SocketChannel sock = SocketChannel.open();
			sock.configureBlocking(false);					
			sock.socket().setTcpNoDelay(true);
			sock.socket().setKeepAlive(true);

          //  Map<String,String> metaData = getNameIPPort();
    		metaData.put("DEVICE_TYPE","5");         
            JCL_message_metadata msg = new MessageMetadataImpl();
	    	msg.setType(-1);				
			msg.setMetadados(metaData);

			this.selector.wakeup();
            SelectionKey sk = sock.register(this.selector,SelectionKey.OP_CONNECT);			

            this.routerLink.setSocket(sock);
            this.routerLink.setSk(sk);
            this.routerLink.setSel(this.selector);
//          this.routerLink.setLock(this.selectorLock);
            this.routerLink.setServerR(this.serverR);
            this.routerLink.setMsg(msg);
            this.routerLink.setMac(macConvert(metaData.get("MAC")));

            sk.attach(this.routerLink); 
            sock.connect(new java.net.InetSocketAddress(serverAdd, routerPort));
            this.selector.wakeup();
            
            for(int i = 0;i < routerLinks;i++){
            	
            	//Create new socket link
            	SocketChannel sockN = SocketChannel.open();
    			sockN.configureBlocking(false);					
    			sockN.socket().setTcpNoDelay(true);
    			sockN.socket().setKeepAlive(true);
    			
    			//wakeup a Selection
    			this.selector.wakeup();
    			SelectionKey skN = sockN.register(this.selector,SelectionKey.OP_CONNECT);    			
    			JCL_connector conecN = new JCL_connector(sockN,skN,this.selector,this.serverR,null, macConvert(metaData.get("MAC")));
                skN.attach(conecN);            
                sockN.connect(new java.net.InetSocketAddress(serverAdd, routerPort));
    			this.selector.wakeup();

            }
            
            
            
           LK =  this.selector.keys();
           
           for(SelectionKey k:LK){
        	  System.out.println("int OP:"+k.interestOps());
           }
            
            ShutDownHook();
//           sk.attach(new JCL_acceptor(this.serverSocket,this.selectorRead,this.selectorReadLock,this.serverR));

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	private void ShutDownHook() {
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	    	
	      @Override
	      public void run() {
	    	try {
			
	    	//	Map<String,String> metaData = getNameIPPort();
    		//	metaData.put("DEVICE_TYPE","5");         
            	JCL_message_metadata msg = new MessageMetadataImpl();
	    		msg.setType(-2);				
				msg.setMetadados(metaData);			
				routerLink.send(msg);
				Thread.sleep(1000);			
	    	} 	    	
	    	catch (Exception e) {
				System.err.println("Erro in unregister host!");
			}
	      }
	    });
	  }
	
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
			
//			IPPort.put("IP", ip.getHostAddress());
			IPPort.put("MAC", sb.toString());
			IPPort.put("CORE(S)", "0");

			return IPPort;
			
	 
		} catch (Exception e) {
			
			try {
				InetAddress ip = InetAddress.getLocalHost();
			
				String sb = ip.getHostAddress();
	//			IPPort.put("IP", sb);
				IPPort.put("MAC", sb);
				IPPort.put("CORE(S)", "0");

				return IPPort;
			} catch (UnknownHostException e1) {
				System.err.println("cannot collect host address");
				return null;
			}
			
			
		}
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
