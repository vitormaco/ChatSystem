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
	private NetworkListener listener;
	private KeepAliveService discoverService;
	private ChatView chatView = null;
	private Dotenv dotenv = Dotenv.load();

	public MessageService() {
		this.usersList = new HashMap<String, UserMessages>();
		this.listener = this.getListenerThread();
		this.listener.start();
		this.discoverService = new KeepAliveService(this);
	}
	
	private NetworkListener getListenerThread () {
		int broadcastPort = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
		return new NetworkListener(broadcastPort, this);
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

	public Set<String> getAllActiveUsers() {
		Set<String> nicknames = new HashSet<String>();
		for (UserMessages user : usersList.values()) {
			nicknames.add(user.getNickname());
		}
		return nicknames;
	}

	private boolean isNicknameAvailable(String nickname) {
		return !this.getAllActiveUsers().contains(nickname);
	}

	private void addNewLoggedUser(String userMAC, String nickname) {
		if (!this.usersList.containsKey(userMAC)) {
			usersList.put(userMAC, new UserMessages(nickname));
			System.out.println("added" + nickname);
		} else {
			usersList.get(userMAC).setNickname(nickname);
		}
		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	private void deleteLoggedoutUser(String id, String nickname) {
		if (usersList.containsKey(id)) {
			usersList.remove(id);
		}
		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	private void receiveUserMessage(MessagePDU message) {
		if (this.chatView != null) {
			usersList.get(message.getSourceMAC())
					.addMessage(new Message(message));
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

	public void sendMessageToUser(String message) {
		String serializedObject;
		serializedObject = new MessagePDU(this.nickname)
				.withMessageType(MessagePDU.MessageType.TEXT)
				.withStatus(MessagePDU.Status.MESSAGE)
				.withMessageContent(message)
				.withDestinationBroadcast()
				.serialize();
		NetworkUtils.sendBroadcastMessage(serializedObject);
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
		while (this.listener.isAlive())
			;

		while (this.discoverService.isAlive())
			;
	}

	public void messageReceived(MessagePDU message) {
		MessagePDU.Status status = message.getStatus();

		if (status == MessagePDU.Status.CONNECTION) {
			this.addNewLoggedUser(message.getSourceMAC(),
					message.getSourceNickname());
		} else if (status == MessagePDU.Status.DECONNECTION) {
			this.deleteLoggedoutUser(message.getSourceMAC(),
					message.getSourceNickname());
		} else if (status == MessagePDU.Status.MESSAGE) {
			this.receiveUserMessage(message);
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

	public void setChatView(ChatView chatView) {
		this.chatView = chatView;
		this.chatView.updateConnectedUsersList();
	}

}
