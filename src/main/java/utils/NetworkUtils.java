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

	public static String getIPAddress() {
		if (networkIP != null) {
			return networkIP;
		}
		try {
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			while (n.hasMoreElements()) {
				NetworkInterface interf = n.nextElement();

				Enumeration<InetAddress> a = interf.getInetAddresses();
				while (a.hasMoreElements()) {
					InetAddress addr = a.nextElement();
					String ip = addr.getHostAddress();
					if (ip.startsWith(dotenv.get("BASE_IP"))) {
						networkIP = ip;
						return ip;
					}
				}
			}

			throw new Exception("Configured network not found, please check if it's properly set up");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
        }
	}

	public static void sendBroadcastMessage(String msg) {
		sendUDPMessage(msg, dotenv.get("BASE_IP") + ".255");
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
