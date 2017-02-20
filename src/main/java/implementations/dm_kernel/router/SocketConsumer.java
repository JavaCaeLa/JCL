package implementations.dm_kernel.router;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;
import commom.JCL_resultImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.util.XORShiftRandom;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_result;

public class SocketConsumer<S extends JCL_handler> extends GenericConsumer<S> {

	private ConcurrentMap<String, List<JCL_handler>> superPeer;
	private ConcurrentMap<String, String> superPeerHost;
	private AtomicInteger peerID;
	private GenericResource<JCL_handler> reS;
	private static XORShiftRandom rand;
	private Integer routerPort;
	private String ipR;

	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, ConcurrentMap<String, List<JCL_handler>> superPeer,
			AtomicInteger peerID, GenericResource<JCL_handler> reS, Integer routerPort, String ipR, ConcurrentMap<String, String> superPeerHost) {
		// TODO Auto-generated constructor stub
		super(re, kill);
		this.superPeer = superPeer;
		this.superPeerHost = superPeerHost;
		this.peerID = peerID;
		this.reS = reS;
		this.routerPort = routerPort;
		this.ipR = ipR;

		// Start seed rand GV
		rand = new XORShiftRandom();
	}

	@Override
	protected void doSomething(S str) {
		try {
			// TODO Auto-generated method stub
			// System.out.println("Tipo da msg:"+str.getKey());
			
			switch (str.getInput()[1]) {

			case -1:{
				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
				superPeerHost.put(msg.getMetadados().get("MAC")+msg.getMetadados().get("PORT"), msg.getMetadados().get("SUPER_PEER"));
				msg.getMetadados().put("PORT_SUPER_PEER",msg.getMetadados().get("PORT"));
				msg.getMetadados().put("PORT", this.routerPort.toString());
				msg.getMetadados().put("IP", this.ipR);
				this.reS.putRegister(str);
				
				break;
			}
			
			case -2:{
				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
				superPeerHost.remove(msg.getMetadados().get("MAC")+msg.getMetadados().get("PORT"));
				msg.getMetadados().put("PORT_SUPER_PEER",msg.getMetadados().get("PORT"));
				msg.getMetadados().put("PORT", this.routerPort.toString());
				msg.getMetadados().put("IP", this.ipR);
				this.reS.putRegister(str);
				
				break;
			}
			
//			case 39: {
//				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
//				msg.getMetadados().put("PORT", this.routerPort.toString());
//				msg.getMetadados().put("IP", this.ipR);
//				this.reS.putRegister(str);
//				break;
//			}

			//Register Super Peer
			case -4: {
				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
				System.out.println("Add Super Peer:");
//				msg.getMetadados().put("PORT", this.routerPort.toString());
//				msg.getMetadados().put("IP", this.ipR);
//				msg.setType(-1);
				System.out.println(msg.getMetadados().toString());
				
				JCL_message msgR = new MessageImpl();
				msgR.setType(-4);
				
				//Write data
				super.WriteObjectOnSock(msgR, str,true);
				//End Write data
				
			//	this.reS.putRegister(str);
				break;
			}
			
			//UnRegister Super Peer
			case -5: {
				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
				
				System.out.println("Remove Super Peer:");
//				msg.getMetadados().put("PORT", this.routerPort.toString());
//				msg.getMetadados().put("IP", this.ipR);
//				msg.setType(-1);
				System.out.println(msg.getMetadados().toString());
//				JCL_message_control msgF = new MessageControlImpl();
//				msgF.setType(-2);
//				String[] hostIp = { this.ipR, this.routerPort.toString(), msg.getMetadados().get("MAC"),
//						msg.getMetadados().get("CORE(S)"), msg.getMetadados().get("DEVICE_TYPE") };
//				msgF.setRegisterData(hostIp);
//				str.setMsg(msgF);
				
				
				JCL_message msgR = new MessageImpl();
				msgR.setType(-5);
				
				//Write data
				super.WriteObjectOnSock(msgR, str,true);
				//End Write data
				this.reS.putRegister(str);
				
				break;
			}
			
			//Register Host from Super Peer
//			case -7: {
//				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
//				superPeerHost.put(msg.getMetadados().get("MAC"), str.getMacS());
//				msg.getMetadados().put("PORT", this.routerPort.toString());
//				msg.getMetadados().put("IP", this.ipR);
//				msg.setType(-1);
//				this.reS.putRegister(str);
//				break;
//			}
//			
//			//UnRegister Host from Super Peer
//			case -8: {
//				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
//				JCL_message_control msgF = new MessageControlImpl();
//				msgF.setType(-2);
//				String[] hostIp = { this.ipR, this.routerPort.toString(), msg.getMetadados().get("MAC"),
//						msg.getMetadados().get("CORE(S)"), msg.getMetadados().get("DEVICE_TYPE") };
//				msgF.setRegisterData(hostIp);
//				str.setMsg(msgF);
//				this.reS.putRegister(str);
//				break;
//			}
						
//			case -6: {
//				synchronized (str) {
//					if (str.getFrom() == null) {
//						// System.out.println("mac:"+str.getMacS());
//						JCL_handler peer = superPeer.get(str.getMacS())
//								.get(peerID.incrementAndGet() % superPeer.get(str.getMacS()).size());
//						synchronized (peer) {
//							if (peer.getFrom() == null) {
//								// OLHAR PROBLEMA RETIREI ATENCAO
//					//			peer.send(str.getInput(), str.getKey(), (short)rand.nextInt(3000,1,1), str.getMac());
//								peer.setFrom(str);
//							} else {
//								str.putOnQueue();
//							}
//						}
//
//					} else {
//						// System.out.println("Send Back!!!");
//						str.sendBack();
//						str.setFrom(null);
//					}
//				}
//
//				break;
//			}

			case -100:{
				
				JCL_message_metadata msg = (JCL_message_metadata) str.getMsg();
				String key = msg.getMetadados().get("MAC")+msg.getMetadados().get("PORT");
				
				synchronized (superPeer) {
					
					if (superPeer.get(key) == null) {					
						superPeer.put(key, new LinkedList<JCL_handler>());						
						superPeer.get(key).add(str);						
					}else {
						superPeer.get(key).add(str);
					}
				}

				break;
			}

			default: {
				synchronized (str) {						
						String superpeerKey = superPeerHost.get(str.getMacS()+str.getport());
						
						JCL_handler peer = superPeer.get(superpeerKey)
								.get(peerID.incrementAndGet() % superPeer.get(superpeerKey).size());
						synchronized (peer) {
							if (peer.getFrom() == null) {
//								System.out.println("mac envia:"+str.getMacS());
								peer.sendB(str.getMsgHeard());
								peer.sendB(str.getMsgRe());
								peer.setFrom(str);
						//		peer.sendTo();
//								System.out.println("mac envia2:"+str.getMacS());
							} else {
								str.putOnQueue();
							}
						}

//					} else {
//						System.out.println("Send Back!!!");
//						str.sendBack();
//						str.setFrom(null);
//					}
				}
				break;
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
