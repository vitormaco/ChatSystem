package models;

import java.sql.Timestamp;

import models.MessagePDU.MessageType;

public class Message {
	private Timestamp timestamp;
	private String content;
	private MessageType type;
	private boolean isClient;

	public Message(MessagePDU message) {
		timestamp = message.getTimestamp();
		content = message.getMessageContent();
		type = message.getMessageType();
	}

	public Message(String message, boolean client) {
		timestamp = new Timestamp(System.currentTimeMillis());
		type = MessageType.TEXT;
		content = message;
		isClient = client;
	}

	public String getFormattedMessage() {
		return content;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public boolean isClient() {
		return isClient;
	}
}
