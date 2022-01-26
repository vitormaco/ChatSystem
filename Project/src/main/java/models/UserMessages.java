package models;

import java.util.ArrayList;
import utils.ConfigManager;

public class UserMessages {
	private ArrayList<Message> messages = new ArrayList<Message>();
	private String nickname;
	private String addressIp;
	private long lastSeenAlive;
	private ConfigManager properties = new ConfigManager();
	private int aliveTime = Integer.parseInt(properties.get("ALIVE_TIME"));

	public UserMessages(String nickname, String adressIp) {
		this.nickname = nickname;
		this.addressIp = adressIp;
		this.lastSeenAlive = System.currentTimeMillis();
	}

	public String getAddressIp() {
		return addressIp;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void resetLastSeenAlive() {
		this.lastSeenAlive = System.currentTimeMillis();
	}

	public boolean wasSeenRecently() {
		return (System.currentTimeMillis() - lastSeenAlive) > (aliveTime*2.5);
	}

	public void addMessage(Message message) {
		this.messages.add(message);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
