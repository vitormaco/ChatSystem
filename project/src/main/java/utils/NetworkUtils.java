package utils;

import java.net.*;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class NetworkUtils {
	static Dotenv dotenv = Dotenv.load();
	static String networkIP = null;

	public static String getLocalMACAdress() {
		byte[] mac;
		StringBuilder sb = new StringBuilder();

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface network = networkInterfaces.nextElement();
				mac = network.getHardwareAddress();
				if (mac != null) {
					sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s",
								mac[i], (i < mac.length - 1) ? ":" : ""));
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	private static InetAddress getInetAddress() {
		Enumeration<NetworkInterface> n;
		try {
			n = NetworkInterface.getNetworkInterfaces();
			while (n.hasMoreElements()) {
				NetworkInterface interf = n.nextElement();

				Enumeration<InetAddress> a = interf.getInetAddresses();
				while (a.hasMoreElements()) {
					InetAddress addr = a.nextElement();
					String ip = addr.getHostAddress();
					if (ip.startsWith(dotenv.get("BASE_IP"))) {
						return addr;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getBroadcastAddress() {
		InetAddress addr = getInetAddress();
		
	    NetworkInterface networkInterface;
		try {
			networkInterface = NetworkInterface.getByInetAddress(addr);
		    if (networkInterface.isLoopback())
		        return "127.0.0.1";
		    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) 
		    {
		        InetAddress broadcast = interfaceAddress.getBroadcast();
		        if (broadcast == null)
		            continue;
		        return broadcast.getHostAddress();
		    }
			
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return "0.0.0.0";
	}

	public static String getIPAddress() {
		if (networkIP != null) {
			return networkIP;
		}
		try {
			InetAddress addr = getInetAddress();
			
			if(addr != null) {
				networkIP = addr.getHostAddress();
				return networkIP;
			}

			throw new Exception("Configured network not found, please check if it's properly set up");
		} catch (Exception e) {
			System.out.println(e.toString());
			return "127.0.0.1";
        }
	}

	public static void sendBroadcastMessage(String msg) {
		sendUDPMessage(msg, getBroadcastAddress());
	}

	public static void sendUnicastMessage(String msg, String ip) {
		sendUDPMessage(msg, ip);
	}

	private static void sendUDPMessage(String msg, String ip) {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(ip);
			byte[] buf = msg.getBytes();
			int broadcastPort = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, broadcastPort);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception thrown when sending broadcast message");
		}
	}
}
