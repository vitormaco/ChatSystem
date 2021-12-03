package services;

import models.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class MessageService {
	// Change to UserMessages Type
	private ArrayList<UserMessages> usersList;
	private String id;
	private String nickname;

	public MessageService() {
		try {
			this.id = this.getMACAdress();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.usersList = new ArrayList<UserMessages>();
	}

	private String getMACAdress() throws Exception {
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
								mac[i], (i < mac.length - 1) ? "-" : ""));
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public String getNickname() {
		return this.nickname;
	}

	public String getId() {
		return this.id;
	}

	/* PUBLIC METHODS */

	public HashSet<String> discoverUsers() {
		HashSet<String> nicknames = new HashSet<String>();
		for (UserMessages user : this.usersList) {
			nicknames.add(user.getNickname());
		}
		return nicknames;
	}

	public boolean validateAndAssingUserNickname(String nickname) {
		Set<String> nicknames = this.discoverUsers();
		if (!nicknames.contains(nickname)) {
			this.nickname = nickname;
			this.notifyUserStateChanged("connected");
			return true;
		} else {
			return false;
		}
	}

	public void notifyUserStateChanged(String state) {

	}

	public void sendBroadcastMessage(String msg) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("0.0.0.0");
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        socket.close();
    }

}
