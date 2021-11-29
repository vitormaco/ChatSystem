package models;

public class UserMessages {
	private String userId;
	private Message[] messages;
	private String nickname;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Message[] getMessages() {
		return messages;
	}
	public void setMessages(Message[] messages) {
		this.messages = messages;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
