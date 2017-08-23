package implementations.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import commom.Constants;


public class UDPServer implements Runnable{
	DatagramSocket socket;
	int serverPort;
	int routerPort;

	public UDPServer(int jclServerPort, int routerPort){
		serverPort = jclServerPort;
		this.routerPort = routerPort;
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(Constants.Environment.UDPPORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {
				byte[] recvBuf = new byte[48];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				String message = new String(packet.getData()).trim();
				if (message.equals("SERVERMAINPORT")) {
					byte[] sendData = String.valueOf(serverPort).trim().getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
				}
				if (message.equals("SERVERROUTERPORT")) {
					byte[] sendData = String.valueOf(routerPort).trim().getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);					
				}
			}
		} catch (IOException ex) {
		}
	}
}
