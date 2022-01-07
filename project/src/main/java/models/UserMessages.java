package models;

import java.util.ArrayList;

public class UserMessages {
	private String userId;
	private ArrayList<Message> messages = new ArrayList<Message>();
	private String nickname;
	private String addressIp;

	public UserMessages(String nickname, String adressIp) {
		this.nickname = nickname;
		this.addressIp = adressIp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAddressIp() {
		return addressIp;
	}

	public void setAdressIp(String addressIp) {
		this.addressIp = addressIp;
	}

	public ArrayList<Message> getMessages() {
		return messages;
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
