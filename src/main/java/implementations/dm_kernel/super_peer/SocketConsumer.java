package implementations.dm_kernel.super_peer;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;
import implementations.dm_kernel.ConnectorImpl;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message_metadata;

public class SocketConsumer<S extends JCL_handler> extends GenericConsumer<S> {

	private ConcurrentMap<String, Map<String, String>> slaves;
	private commom.JCL_connector routerLink;
	private String superpeerID, ServerIP;
	private int ServerPort;
	
	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, commom.JCL_connector routerLink,ConcurrentMap<String, Map<String, String>> slaves,String superpeerID, String ServerIP, int ServerPort){
		// TODO Auto-generated constructor stub
		super(re, kill);
		this.slaves = slaves;
		this.routerLink = routerLink;
		this.superpeerID = superpeerID;
		this.ServerIP = ServerIP;
		this.ServerPort = ServerPort;
	}

	@Override
	protected void doSomething(S str) {
		try {

				// Get local time
				Locale locale = new Locale("pt", "BR");
				GregorianCalendar calendar = new GregorianCalendar();
				SimpleDateFormat formatador = new SimpleDateFormat("dd' de 'MMMMM' de 'yyyy' - 'HH':'mm'h'", locale);
								
				switch (str.getInput()[1]) {

				case -1: {
					synchronized(routerLink){
					JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
					msg.getMetadados().put("SUPER_PEER",this.superpeerID);
					routerLink.send(msg);
					routerLink.getHandler().setFrom(str);
					slaves.put(msg.getMetadados().get("MAC")+msg.getMetadados().get("PORT"), msg.getMetadados());
					}

					break;
				}
				
				case -2: {
					synchronized(routerLink){
					JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
					msg.getMetadados().put("SUPER_PEER",this.superpeerID);
					routerLink.send(msg);
					routerLink.getHandler().setFrom(str);
					slaves.remove(msg.getMetadados().get("MAC")+msg.getMetadados().get("PORT"));

					}

					break;
				}


				case -4: {
						System.out.println("SUPER PEER JCL is OK");
					break;
				}

				case -5: {
						System.out.println("SUPER PEER JCL WAS UNREGISTERED!");

					break;
				}
				
				
				default:{
					Map<String,String> meta = slaves.get(str.getMacS()+str.getport());
					String host = null, mac = null;
					int port = 0;
					
					if (meta!=null){
						host = meta.get("IP");
						port = Integer.parseInt(meta.get("PORT"));
						mac = meta.get("MAC");
					}else{
						
			//			System.out.println("Server!!");
						
						host = ServerIP;
						port = ServerPort;
						mac = null;
					}
					
//					System.out.println("Connect to host");
//					System.out.println("Host:"+host);
//					System.out.println("Port:"+port);
    	   		  	
						JCL_connector connector = new ConnectorImpl();
						if (connector.connect(host,port,mac)){
					
						ByteBuffer msg = ByteBuffer.allocate(str.getMsgHeard().limit() + str.getMsgRe().limit());
				    
						str.getMsgHeard().flip();
						str.getMsgRe().flip();
					
						msg.put(str.getMsgHeard());
						msg.put(str.getMsgRe());
						str.sendB(connector.sendReceiveB(msg));
						} else{
							str.sendB(ByteBuffer.allocate(4).putInt(0));
						}
				}
				
//				case -7: {
//
//					JCL_message_bool msgr = (JCL_message_bool) msg;
//
//					if (msgr.getRegisterData()[0]) {
//						System.out.println("UPDATE CORE OK!");
//					} else {
//						System.err.println("PROBLEM IN UPDATE CORE!");
//					}
//					break;
//				}
				}
		//	} Fim do if 

		} catch (Exception e) {
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
}
