package models;

import java.sql.Timestamp;

public class Message {
	private Timestamp timestamp;
	private String content;
	private boolean isClient;

	public Message(MessagePDU message) {
		timestamp = message.getTimestamp();
		content = message.getMessageContent();
	}

	public Message(String message, boolean client) {
		timestamp = new Timestamp(System.currentTimeMillis());
		content = message;
		isClient = client;
	}

	public String getContent() {
		return content;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public boolean isClient() {
		return isClient;
	}
}
