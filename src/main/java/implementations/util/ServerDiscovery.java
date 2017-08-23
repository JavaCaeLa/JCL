package implementations.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import commom.Constants;

public class ServerDiscovery {

	public static String[] discoverServer() {
		System.out.println("Starting Server Discovery");
		String serverData[] = null;
		try {
			DatagramSocket c;
			c = new DatagramSocket();
			c.setBroadcast(true);

			byte[] sendData = "SERVERMAINPORT".getBytes();

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Constants.Environment.UDPPORT);
					c.send(sendPacket);
				}
			}

			byte[] recvBuf = new byte[48];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			c.setSoTimeout(8000);
			c.receive(receivePacket);

			serverData = new String[2];
			serverData[0] = receivePacket.getAddress().getHostAddress();
			serverData[1] = new String(receivePacket.getData()).trim();

			c.close();
		} catch (IOException ex) {
			System.out.println("Could not find JCL Server in the network");
		}
		return serverData;
	}
	
	public static String[] discoverServerRouterPort() {
		System.out.println("Starting Server Discovery");
		String serverData[] = null;
		try {
			DatagramSocket c;
			c = new DatagramSocket();
			c.setBroadcast(true);

			byte[] sendData = "SERVERROUTERPORT".getBytes();

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Constants.Environment.UDPPORT);
					c.send(sendPacket);
				}
			}

			byte[] recvBuf = new byte[48];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			c.setSoTimeout(8000);
			c.receive(receivePacket);

			serverData = new String[2];
			serverData[0] = receivePacket.getAddress().getHostAddress();
			serverData[1] = new String(receivePacket.getData()).trim();

			c.close();
		} catch (IOException ex) {
			System.out.println("Could not find JCL Server in the network");
		}
		return serverData;
	}

}
