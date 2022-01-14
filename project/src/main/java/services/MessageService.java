package services;

import models.UserMessages;
import utils.NetworkUtils;
import models.Message;
import models.MessagePDU;

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
	private boolean shouldUseDatabase = !dotenv.get("USE_DATABASE").isBlank();

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

	private NetworkTCPListener getListenerTCPThread() {
		int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
		return new NetworkTCPListener(tcpPort, this);
	}

	public void notifyUserStateChanged(MessagePDU.Status status) {
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

	private void addNewLoggedUser(String userMAC, String new_nickname, String addressIp) {

		if (userMAC.equals(myMac)) {
			return;
		}

		if (!this.usersList.containsKey(userMAC)) {
			usersList.put(userMAC, new UserMessages(new_nickname, addressIp));
			updateConnectedUsersFrontend();
		} else {
			String actual_nickname = usersList.get(userMAC).getNickname();
			if(!actual_nickname.equals(new_nickname)) {
				usersList.get(userMAC).setNickname(new_nickname);
				nicknameUpdated(actual_nickname, new_nickname);
			}
		}
	}

	public void deleteLoggedoutUser(String id) {
		usersList.remove(id);
		updateConnectedUsersFrontend();
	}

	private void updateConnectedUsersFrontend() {
		if (this.chatView != null) {
			this.chatView.updateConnectedUsersList();
		}
	}

	private void nicknameUpdated(String old_nickname, String new_nickname) {
		this.usersList.put(new_nickname, usersList.remove(old_nickname));
		this.chatView.nicknameChanged(old_nickname, new_nickname);
		this.chatView.updateSelectedUserMessages();
	}

	public void receiveUserMessage(String mac, Message message) {
		usersList.get(mac).addMessage(message);
		if (this.shouldUseDatabase) {
			HistoryService.saveMessage(NetworkUtils.getLocalMACAdress(), mac, message);
			System.out.println("message saved to database");
		}

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

	public void sendMessageToUser(String message, String mac) {
		String serializedObject;
		serializedObject = new MessagePDU(this.nickname)
				.withStatus(MessagePDU.Status.MESSAGE)
				.withMessageContent(message)
				.serialize();
		NetworkUtils.sendBroadcastMessage(serializedObject);
	}

	private boolean isNicknameAvailable(String nickname) {
		return !this.getAllActiveUsers().containsKey(nickname);
	}

	public boolean validateAndAssingUserNickname(String nickname, MessagePDU.Status state) {
		if (this.isNicknameAvailable(nickname)) {
			this.nickname = nickname;
			if (state == MessagePDU.Status.CONNECTION) {
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

	public void broadcastMessageReceived(MessagePDU message) {
		MessagePDU.Status status = message.getStatus();
		String mac = message.getSourceMAC();
		String nickname = message.getSourceNickname();
		String address = message.getSourceAddress();

		if (status == MessagePDU.Status.CONNECTION) {
			this.addNewLoggedUser(mac, nickname, address);
		} else if (status == MessagePDU.Status.DISCONNECTION) {
			this.deleteLoggedoutUser(mac);
		} else if (status == MessagePDU.Status.DISCOVER) {
			this.sendMyNickname(message);
		}
	}

	public String getMyNickname() {
		return this.nickname;
	}

	public ArrayList<Message> getUserMessages(String mac) {
		// return HistoryService.getHistory();
		if (usersList.containsKey(mac))
			return usersList.get(mac).getMessages();
		return new ArrayList<Message>();
	}

	public void setChatView() {
		this.chatView = new ChatView(this);
	}

	public void createTCPConnection(String mac) {
		try {
			if (activeChat != null) {
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
		System.out.println("Me: " + " Message: " + message);
	}
}
