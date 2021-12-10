package services;

import models.*;
import java.net.*;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;
import view.ChatView;

public class MessageService {
	// Change to UserMessages Type
	private ArrayList<UserMessages> usersList;
	private String id;
	private String nickname;
	private NetworkListener listener;
	private ChatView chatView = null;
	Dotenv dotenv = Dotenv.load();

	public MessageService() {
		try {
			this.id = this.getMACAdress();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.usersList = new ArrayList<UserMessages>();
		int broadcastPort = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
		this.listener = new NetworkListener(broadcastPort, this);
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
								mac[i], (i < mac.length - 1) ? ":" : ""));
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

		try {
			switch (state) {
				case "connected":
					serializedObject = new MessagePDU(this)
							.withStatus(MessagePDU.Status.CONNECTION)
							.withMessageType(MessagePDU.MessageType.NOTIFICATION)
							.withDestinationBroadcast()
							.serialize();
					break;
				case "disconnected":
					serializedObject = new MessagePDU(this)
							.withStatus(MessagePDU.Status.DECONNECTION)
							.withMessageType(MessagePDU.MessageType.NOTIFICATION)
							.withDestinationBroadcast()
							.serialize();
					break;
				case "nicknameChanged":
					serializedObject = new MessagePDU(this)
							.withStatus(MessagePDU.Status.NICKNAME_CHANGED)
							.withMessageType(MessagePDU.MessageType.NOTIFICATION)
							.withDestinationBroadcast()
							.serialize();
					break;
				case "discover":
					serializedObject = new MessagePDU(this)
							.withStatus(MessagePDU.Status.DISCOVER)
							.withMessageType(MessagePDU.MessageType.NOTIFICATION)
							.withDestinationBroadcast()
							.serialize();
					break;
				default:
					throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Error creating PDU");
			e.printStackTrace();
		}

		this.sendBroadcastMessage(serializedObject);
	}

	private void sendBroadcastMessage(String msg) {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName("255.255.255.255");
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

	private void sendUnicastMessage(String msg, String ip) {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(ip);
			byte[] buf = msg.getBytes();
			int port = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception thrown when sending broadcast message");
		}
	}

	private void addNewLoggedUser(String nickname) {
		if (this.chatView != null) {

		}
	}

	private void deleteLoggedoutUser(String nickname) {
		if (this.chatView != null) {

		}
	}

	private void sendMyNickname(String address) {
		String serializedObject = new MessagePDU(this)
				.withStatus(MessagePDU.Status.CONNECTION)
				.withMessageType(MessagePDU.MessageType.NOTIFICATION)
				.withDestination("nickname", "id", address)
				.serialize();

		this.sendUnicastMessage(serializedObject, address);
	}

	/* PUBLIC METHODS */

	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return null;
		}
	}

	public void discoverUsers() {
		this.notifyUserStateChanged("discover");
	}

	public HashSet<String> getNicknamesofActiveUsers() {
		HashSet<String> nicknames = new HashSet<String>();
		for (UserMessages user : this.usersList) {
			nicknames.add(user.getNickname());
		}
		return nicknames;
	}

	public boolean validateAndAssingUserNickname(String nickname, String state) {
		this.discoverUsers();
		Set<String> nicknames = this.getNicknamesofActiveUsers();
		if (!nicknames.contains(nickname)) {
			this.nickname = nickname;
			if (state == "connected") {
				this.listener.start();
			}
			this.notifyUserStateChanged(state);
			return true;
		} else {
			return false;
		}
	}

	public void disconnectServer() {
		this.listener.setRunning(false);
	}

	public void messageReceived(MessagePDU message) {
		MessagePDU.Status status = message.getStatus();

		if (status == MessagePDU.Status.CONNECTION) {
			this.addNewLoggedUser(message.getSourceNickname());
		} else if (status == MessagePDU.Status.DECONNECTION) {
			this.deleteLoggedoutUser(message.getSourceNickname());
		} else if (status == MessagePDU.Status.DISCOVER) {
			this.sendMyNickname(message.getSourceAddress());
		}
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
