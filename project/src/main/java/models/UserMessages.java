package models;

import java.util.ArrayList;

public class UserMessages {
	private String userId;
	private ArrayList<Message> messages = new ArrayList<Message>();
	private String nickname;

	public UserMessages(String nickname) {
		this.nickname = nickname;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
