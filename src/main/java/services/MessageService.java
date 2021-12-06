package services;

import models.*;
import java.net.*;
import java.util.*;
import java.io.*;
import view.ChatView;

public class MessageService {
	// Change to UserMessages Type
	private ArrayList<UserMessages> usersList;
	private String id;
	private String nickname;
    private NetworkListener listener;
    private ChatView chatView = null;

	public MessageService() {
		try {
			this.id = this.getMACAdress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.usersList = new ArrayList<UserMessages>();
		
		this.listener = new NetworkListener(4446, this);	
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

	public void notifyUserStateChanged(String state) {
		String serializedObject = "";
		
		if(state == "connected") {
			serializedObject = new MessagePDU()
					.withMessageContent("")
					.withStatus(MessagePDU.Status.CONNECTION)
					.withSourceNickname(this.nickname)
					.withSourceID(this.id)
					.serialize();
		}else if(state == "disconnected") {
			serializedObject = new MessagePDU()
					.withMessageContent("")
					.withStatus(MessagePDU.Status.DECONNECTION)
					.withSourceNickname(this.nickname)
					.withSourceID(this.id)
					.serialize();
		}

	    this.sendBroadcastMessage(serializedObject, 4446);
		System.out.println(serializedObject);
	}

	private void sendBroadcastMessage(String msg, int port) {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName("255.255.255.255");
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception thrown when sending broadcast message");
		}
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
		    this.listener.start();
			return true;
		} else {
			return false;
		}
	}
	
	public void disconnectServer() {
		this.listener.setRunning(false);
	}
	
	public void messageReceived(MessagePDU message) {
        System.out.println("listener received: " + message.getSourceNickname());
	}
	

	public String getNickname() {
		return this.nickname;
	}

	public String getId() {
		return this.id;
	}
	
	public void setChatView(ChatView chatView) {
		this.chatView = chatView;
	}

}
