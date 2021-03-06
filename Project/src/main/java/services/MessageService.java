package services;

import models.UserMessages;
import utils.NetworkUtils;
import models.Message;
import models.MessagePDU;

import java.net.ConnectException;
import java.util.*;

import utils.ConfigManager;
import view.ChatView;

public class MessageService {
	private HashMap<String, UserMessages> usersList;
	private String nickname;
	private ClientTCP activeChat;
	private NetworkUDPListener listener;
	private NetworkTCPListener listenerTCP;
	private ChatView chatView = null;
	private ConfigManager properties = new ConfigManager();
	private String myMac = NetworkUtils.getLocalMACAdress();
	private boolean shouldUseDatabase = !properties.get("USE_DATABASE").isBlank();
	private ImAliveService aliveService;

	public MessageService() {
		this.usersList = new HashMap<String, UserMessages>();
		this.listener = this.getListenerThread();
		this.listener.start();
		this.listenerTCP = this.getListenerTCPThread();
		this.listenerTCP.start();
		this.aliveService = new ImAliveService(this);
		this.aliveService.start();
	}

	public boolean isConnected() {
		return NetworkUtils.getIPAddress() != "-";
	}

	private NetworkUDPListener getListenerThread() {
		int broadcastPort = Integer.parseInt(properties.get("BROADCAST_PORT"));
		return new NetworkUDPListener(broadcastPort, this);
	}

	private NetworkTCPListener getListenerTCPThread() {
		int tcpPort = Integer.parseInt(properties.get("TCP_PORT"));
		return new NetworkTCPListener(tcpPort, this);
	}

	public void notifyUserStateChanged(MessagePDU.Status status) {
		System.out.println("broadcast sent of type: " + status);
		String serializedObject;

		serializedObject = new MessagePDU(this.nickname)
				.withStatus(status)
				.serialize();

		NetworkUtils.sendBroadcastMessage(serializedObject);
	}

	public HashMap<String, String> getAllActiveUsers() {
		HashMap<String, String> nicknames = new HashMap<String, String>();
		for (Map.Entry<String, UserMessages> user : usersList.entrySet()) {
			nicknames.put(user.getValue().getNickname(), user.getKey());
		}
		return nicknames;
	}

	public String getMACByNickname(String nickname) {
		HashMap<String, String> users = getAllActiveUsers();
		if (users.containsKey(nickname))
			return users.get(nickname);
		return null;
	}

	public void addOrUpdateUser(String userMAC, String new_nickname, String addressIp) {

		if (userMAC.equals(myMac)) {
			return;
		}

		if (!this.usersList.containsKey(userMAC)) {
			usersList.put(userMAC, new UserMessages(new_nickname, addressIp));
		} else {
			String old_nickname = usersList.get(userMAC).getNickname();
			if (!new_nickname.equals(old_nickname)) {
				usersList.get(userMAC).setNickname(new_nickname);
				this.chatView.updateSelectedUserNickname(old_nickname, new_nickname);
			}
		}

		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	public void deleteLoggedoutUser(String id) {
		System.out.println("DELETED USER " + id);
		usersList.remove(id);
		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	public void handleNewUserMessage(String mac, Message message) {
		usersList.get(mac).addMessage(message);

		if (this.chatView != null && mac.equals(this.chatView.getSelectedUserMAC())) {
			this.chatView.updateSelectedUserMessages();
		}
	}

	private void sendMyNickname(MessagePDU message) {
		String serializedObject = new MessagePDU(this.nickname)
				.withStatus(MessagePDU.Status.CONNECTION)
				.serialize();

		NetworkUtils.sendUnicastMessage(serializedObject, message.getSourceAddress());
	}

	private boolean isNicknameAvailable(String nickname) {
		return !this.getAllActiveUsers().containsKey(nickname);
	}

	public boolean validateAndAssingUserNickname(String nickname, MessagePDU.Status state) {
		if (this.isNicknameAvailable(nickname)) {
			this.nickname = nickname;
			this.notifyUserStateChanged(state);
			return true;
		} else {
			return false;
		}
	}

	public void receiveBroadcastMessage(MessagePDU message) {
		MessagePDU.Status status = message.getStatus();
		String mac = message.getSourceMAC();
		String nickname = message.getSourceNickname();
		String address = message.getSourceAddress();

		System.out.println("broadcast received of type: " + status);

		if (status == MessagePDU.Status.CONNECTION) {
			this.addOrUpdateUser(mac, nickname, address);
		} else if (status == MessagePDU.Status.DISCONNECTION) {
			this.deleteLoggedoutUser(mac);
		} else if (status == MessagePDU.Status.DISCOVER) {
			this.addOrUpdateUser(mac, nickname, address);
			this.sendMyNickname(message);
		} else if (status == MessagePDU.Status.NICKNAME_CHANGED) {
			this.addOrUpdateUser(mac, nickname, address);
		}

		this.setLastSeenAlive(mac);
	}

	private void setLastSeenAlive(String mac) {
		if (this.usersList.containsKey(mac)) {
			usersList.get(mac).resetLastSeenAlive();
		}
	}

	public void checkAliveUsers() {
		for (String mac : usersList.keySet()) {
			if (usersList.get(mac).wasSeenRecently()) {
				this.deleteLoggedoutUser(mac);
			}
		}
	}

	public String getMyNickname() {
		return this.nickname;
	}

	public ArrayList<Message> getUserMessages(String mac) {
		if (usersList.containsKey(mac)) {
			return usersList.get(mac).getMessages();
		}

		return new ArrayList<Message>();
	}

	public void setChatView() {
		this.chatView = new ChatView(this);
		try {
			HistoryService.getConnection();
		} catch (Exception e) {
			chatView.showErrorMessage("Error connecting to database, please check your credentials");
		}
	}

	public void createTCPConnection(String mac) {
		try {
			if (activeChat != null) {
				activeChat.closeSocket();
			}
			if (this.usersList.containsKey(mac)) {
				String hostname = usersList.get(mac).getAddressIp();
				int tcpPort = Integer.parseInt(properties.get("TCP_PORT"));
				activeChat = new ClientTCP(hostname, tcpPort, this.myMac, this.nickname, NetworkUtils.getIPAddress());
			}
			if (this.shouldUseDatabase) {
				ArrayList<Message> messagesInHistory = HistoryService.getHistory(myMac, mac);
				System.out.println("retrieved messages from database");

				usersList.get(mac).getMessages().clear();
				for (Message msg : messagesInHistory) {
					usersList.get(mac).addMessage(msg);
				}
				this.chatView.updateSelectedUserMessages();
			}
		} catch (ConnectException e) {
			chatView.showErrorMessage("User disconnected");
			this.deleteLoggedoutUser(mac);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void sendMessageToUserTCP(String message, String mac) {
		if (activeChat.sendMessage(message)) {
			if (this.shouldUseDatabase) {
				HistoryService.saveMessage(NetworkUtils.getLocalMACAdress(), mac, new Message(message, myMac));
				System.out.println("message saved to database");
			}
			handleNewUserMessage(mac, new Message(message, myMac));
			System.out.println("message " + message + " sent to user " + mac);
		} else {
			chatView.showErrorMessage("User disconnected");
			this.deleteLoggedoutUser(mac);
		}

	}
}
