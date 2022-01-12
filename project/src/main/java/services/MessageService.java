package services;

import models.UserMessages;
import models.Message;
import models.MessagePDU;
import utils.NetworkUtils;

import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;
import view.ChatView;

public class MessageService {
	private HashMap<String, UserMessages> usersList;
	private String nickname;
	private ClientTCP activeChat;
	private NetworkListener listener;
	private NetworkTCPListener listenerTCP;
	private KeepAliveService discoverService;
	private ChatView chatView = null;
	private Dotenv dotenv = Dotenv.load();
	private String myMac = NetworkUtils.getLocalMACAdress();

	public MessageService() {
		this.usersList = new HashMap<String, UserMessages>();
		this.listener = this.getListenerThread();
		this.listener.start();
		this.listenerTCP = this.getListenerTCPThread();
		this.listenerTCP.start();
		this.discoverService = new KeepAliveService(this);
	}

	public boolean isConnected() {
		return NetworkUtils.getIPAddress() != "-";
	}

	private NetworkListener getListenerThread() {
		int broadcastPort = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
		return new NetworkListener(broadcastPort, this);
	}

	private NetworkTCPListener getListenerTCPThread(){
		int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
		return new NetworkTCPListener(tcpPort, this);
	}

	public void notifyUserStateChanged(String state) {
		String serializedObject;

		switch (state) {
			case "connected":
				serializedObject = new MessagePDU(this.nickname)
						.withStatus(MessagePDU.Status.CONNECTION)
						.withMessageType(MessagePDU.MessageType.NOTIFICATION)
						.withDestinationBroadcast()
						.serialize();
				break;
			case "disconnected":
				serializedObject = new MessagePDU(this.nickname)
						.withStatus(MessagePDU.Status.DECONNECTION)
						.withMessageType(MessagePDU.MessageType.NOTIFICATION)
						.withDestinationBroadcast()
						.serialize();
				break;
			case "nicknameChanged":
				serializedObject = new MessagePDU(this.nickname)
						.withStatus(MessagePDU.Status.NICKNAME_CHANGED)
						.withMessageType(MessagePDU.MessageType.NOTIFICATION)
						.withDestinationBroadcast()
						.serialize();
				break;
			case "discover":
				serializedObject = new MessagePDU(this.nickname)
						.withStatus(MessagePDU.Status.DISCOVER)
						.withMessageType(MessagePDU.MessageType.NOTIFICATION)
						.withDestinationBroadcast()
						.serialize();
				break;
			default:
				System.out.println("Error creating PDU");
				return;
		}

		NetworkUtils.sendBroadcastMessage(serializedObject);
	}

	public HashMap<String, String> getAllActiveUsers() {
		HashMap<String, String> nicknames = new HashMap<String, String>();
		for (Map.Entry<String, UserMessages> user : usersList.entrySet()) {
			nicknames.put(user.getValue().getNickname(), user.getKey());
		}
		return nicknames;
	}

	private void addNewLoggedUser(String userMAC, String nickname, String addressIp) {

		if (userMAC.equals(myMac)) {
			return;
		}

		if (!this.usersList.containsKey(userMAC)) {
			usersList.put(userMAC, new UserMessages(nickname, addressIp));
		} else {
			usersList.get(userMAC).setNickname(nickname);
		}

		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	public void deleteLoggedoutUser(String id) {
		usersList.remove(id);

		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	public void receiveUserMessage(String mac, Message message) {
		usersList.get(mac).addMessage(message);
		
		if (this.chatView != null && mac.equals(this.chatView.getSelectedUserMAC())) {
			this.chatView.updateSelectedUserMessages();
		}
	}

	private void sendMyNickname(String address) {
		String serializedObject = new MessagePDU(this.nickname)
				.withStatus(MessagePDU.Status.CONNECTION)
				.withMessageType(MessagePDU.MessageType.NOTIFICATION)
				.withDestination("nickname", "id", address)
				.serialize();

		NetworkUtils.sendUnicastMessage(serializedObject, address);
	}

	/* PUBLIC METHODS */

	public void sendMessageToUser(String message, String mac) {
		UserMessages user = usersList.get(mac);
		String serializedObject;
		serializedObject = new MessagePDU(this.nickname)
				.withMessageType(MessagePDU.MessageType.TEXT)
				.withStatus(MessagePDU.Status.MESSAGE)
				.withMessageContent(message)
				.withDestination(user.getNickname(), mac, user.getAddressIp())
				.serialize();
		NetworkUtils.sendBroadcastMessage(serializedObject);
	}

	private boolean isNicknameAvailable(String nickname) {
		return !this.getAllActiveUsers().containsKey(nickname);
	}

	public boolean validateAndAssingUserNickname(String nickname, String state) {
		if (this.isNicknameAvailable(nickname)) {
			this.nickname = nickname;
			if (state == "connected") {
				this.discoverService.start();
			}
			this.notifyUserStateChanged(state);
			return true;
		} else {
			return false;
		}
	}

	public void disconnectServer() {
		this.listener.setRunning(false);
		this.discoverService.setRunning(false);
		this.listenerTCP.setRunning(false);
		
		while (this.listener.isAlive())
			;

		while (this.discoverService.isAlive())
			;

		while (this.listenerTCP.isAlive())
			;
	}

	public void messageReceived(MessagePDU message) {
		MessagePDU.Status status = message.getStatus();

		if (status == MessagePDU.Status.CONNECTION) {
			this.addNewLoggedUser(message.getSourceMAC(),
					message.getSourceNickname(), message.getSourceAddress());
		} else if (status == MessagePDU.Status.DECONNECTION) {
			this.deleteLoggedoutUser(message.getSourceMAC());
		} else if (status == MessagePDU.Status.DISCOVER) {
			this.sendMyNickname(message.getSourceAddress());
		}
	}

	public String getNickname() {
		return this.nickname;
	}

	public ArrayList<Message> getUserMessages(String nickname) {
		for (UserMessages user : usersList.values()) {
			if (user.getNickname() == nickname) {
				return user.getMessages();
			}
		}
		return new ArrayList<Message>();
	}

	public void setChatView() {
		this.chatView = new ChatView(this);
		
		this.chatView = chatView;
		// MOCK
		usersList.put("MAC1", new UserMessages("Mocked User 1", "0.0.0.0"));
		usersList.get("MAC1").addMessage(
				new Message(
						new MessagePDU("Mocked User 1").withMessageContent("TEST1")));
		usersList.get("MAC1").addMessage(
				new Message(
						new MessagePDU("Mocked User 1").withMessageContent("TEST2")));
		usersList.get("MAC1").addMessage(
				new Message(
						new MessagePDU("Mocked User 1").withMessageContent("TEST3")));
		usersList.put("MAC2", new UserMessages("Mocked User 2", "0.0.0.0"));
		this.chatView.updateConnectedUsersList();
		// END OF MOCK
	}

	public void createTCPConnection(String mac){
		try {
			if(activeChat != null) {
				activeChat.closeSocket();
			}
			String hostname = usersList.get(mac).getAddressIp();
			int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
			activeChat = new ClientTCP(hostname, tcpPort, this.myMac);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void sendMessageToUserTCP(String message, String mac) {
		activeChat.sendMessage(message);
		receiveUserMessage(mac, new Message(message, false));
        System.out.println("Me: " +  " Message: " + message);

	}
	

}
