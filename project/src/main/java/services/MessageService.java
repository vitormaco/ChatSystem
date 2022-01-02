package services;

import models.UserMessages;
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
		int broadcastPort = Integer.parseInt(dotenv.get("BROADCAST_PORT"));
		this.listener = new NetworkListener(broadcastPort, this);
		this.discoverService = new KeepAliveService(this);
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
		if (this.chatView != null) {
			if (!this.usersList.containsKey(userMAC)) {
				if (this.isNicknameAvailable(nickname) && this.nickname != nickname) {
					usersList.put(userMAC, new UserMessages(nickname));
				}
			} else {
				usersList.get(userMAC).setNickname(nickname);
			}

			this.chatView.updateConnectedUsersList();
		}
	}

	private void deleteLoggedoutUser(String id, String nickname) {
		if (this.chatView != null) {
			if (usersList.containsKey(id)) {
				usersList.remove(id);
			}
			this.chatView.updateConnectedUsersList();
		}
	}

	private void receiveUserMessage(MessagePDU message) {
		if (this.chatView != null) {
			this.chatView.addMessage(message.getMessageContent());
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

	public void discoverUsers() {
		this.notifyUserStateChanged("discover");
	}

	public boolean validateAndAssingUserNickname(String nickname, String state) {
		this.discoverUsers();
		if (!usersList.containsKey(nickname)) {
			this.nickname = nickname;
			if (state == "connected") {
				this.listener.start();
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

	public void setChatView(ChatView chatView) {
		this.chatView = chatView;
	}

}
