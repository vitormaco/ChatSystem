package utils;

import java.net.*;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class NetworkUtils {
	static Dotenv dotenv = Dotenv.load();

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
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return null;
		}
	}

	public static void sendBroadcastMessage(String msg) {
		sendUDPMessage(msg, "255.255.255.255");
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
